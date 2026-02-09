package io.github.kbuntrock.configuration.library.reader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.model.*;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpringMvcReader extends AbstractLibraryReader {

	// Must be equal to the value defined in spring org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	private static String VALUE_CONSTANT_DEFAULT = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
		primitiveWrapperTypeMap.put(Void.class, void.class);
	}

	private final Method beanUtilsIsSimplePropertyMethod;
	private final Method httpStatusValueMethod;

	public SpringMvcReader(final ApiContext context, final ApiConfiguration apiConfiguration,
		final OpenApiTypeResolver openApiTypeResolver) {
		super(context, apiConfiguration, openApiTypeResolver);
		Class<?> springBeanUtils = context.getClassLoaderHelper().getByNameRuntimeEx("org.springframework.beans.BeanUtils");
		try {
			beanUtilsIsSimplePropertyMethod = springBeanUtils.getMethod("isSimpleProperty", Class.class);
			if(!Modifier.isStatic(beanUtilsIsSimplePropertyMethod.getModifiers())) {
				throw new IllegalStateException("Spring BeanUtils \"isSimpleProperty\" method is expected to be static.");
			}
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException("Spring BeanUtils \"isSimpleProperty\" method load error.", e);
		}

		// HttpStatus block
		Class<?> springHttpStatus = context.getClassLoaderHelper().getByNameRuntimeEx("org.springframework.http.HttpStatus");
		try {
			httpStatusValueMethod = springHttpStatus.getMethod("value");
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException("Spring HttpStatus \"value\" method load error.", e);
		}
	}

	private boolean isSimpleProperty(Class<?> type) {
		try {
			return (boolean) beanUtilsIsSimplePropertyMethod.invoke(null, type);
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new MojoRuntimeException("Cannot invoke spring BeanUtils#isSimpleProperty.", e);
		}
	}

	private int httpStatusValue(Object httpStatus) {
		try {
			return (int) httpStatusValueMethod.invoke(httpStatus);
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new MojoRuntimeException("Cannot invoke spring HttpStatus#value.", e);
		}
	}

	@Override
	public List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations) {
		List<String> basePaths = Collections.singletonList("");
		final MergedAnnotation requestMappingMergedAnnotation = mergedAnnotations
			.get("org.springframework.web.bind.annotation.RequestMapping");
		if(requestMappingMergedAnnotation.isPresent()) {
			final String[] paths = requestMappingMergedAnnotation.getStringArray("value");
			if(paths.length > 0) {
				basePaths = Arrays.asList(paths);
			}
		}
		return basePaths;
	}

	@Override
	public void computeAnnotations(final Class clazz, final String basePath, final Method method,
		final MergedAnnotations mergedAnnotations, final Tag tag) throws MojoFailureException {

		final MergedAnnotation requestMappingMergedAnnotation = mergedAnnotations
			.get("org.springframework.web.bind.annotation.RequestMapping");
		if(requestMappingMergedAnnotation.isPresent() && !excludedByReturnType(method)) {

			final String[] requestMethods = requestMappingMergedAnnotation.getEnumArrayAsString("method");
			if(requestMethods.length > 0) {
				context.getLogger().debug("Parsing request method : " + method.getName());
				final String methodIdentifier = JavaClassAnalyser.createMethodIdentifier(method);
				final List<ParameterObject> parameterObjects = readParameters(clazz, method, mergedAnnotations);
				final DataObject responseObject = readResponseObject(clazz, method, mergedAnnotations);
				final int responseCode = readResponseCode(mergedAnnotations);
				final List<String> paths = readEndpointPaths(basePath, requestMappingMergedAnnotation);
				for(final String requestMethod : requestMethods) {
					for(final String path : paths) {
						final Endpoint endpoint = new Endpoint();
						endpoint.setType(OperationType.fromSpring(requestMethod));
						endpoint.setPath(path);
						endpoint.setName(method.getName());
						endpoint.setParameters(parameterObjects);
						endpoint.setResponseObject(responseObject);
						endpoint.setResponseCode(responseCode);
						setConsumeProduceProperties(endpoint, mergedAnnotations);
						endpoint.setIdentifier(methodIdentifier);
						endpoint.setDeprecated(isDeprecated(method));
						setSwaggerAnnotatedEndpointProperties(endpoint, mergedAnnotations);
						tag.addEndpoint(endpoint);
						context.getLogger()
							.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
					}
				}
			}
		}
	}

	private boolean excludedByReturnType(final Method method) {
		return "org.springframework.web.servlet.ModelAndView".equals(method.getReturnType().getCanonicalName());
	}

	/**
	 * See https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/arguments.html
	 *
	 * @param originalMethod
	 *            inspected method
	 * @return list of parameters to document
	 */
	@Override
	protected List<ParameterObject> readParameters(final Class clazz, final Method originalMethod,
		final MergedAnnotations endpointAnnotations) {
		context.getLogger().debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overriddenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		readRequestMappingParams(endpointAnnotations, parameters);

		readRequestMappingHeaders(endpointAnnotations, parameters);

		for(final Method method : overriddenMethods) {
			for(final Parameter parameter : method.getParameters()) {

				final MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper().from(parameter);

				if(!openApiTypeResolver.canBeDocumented(parameter, mergedAnnotations)) {
					continue;
				}
				context.getLogger().debug("Parameter : " + parameter.getName());

				final ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
					(name) -> unwrapParameterObject(
						new ParameterObject(name, genericityResolver.resolve(clazz, parameter.getParameterizedType()),
							openApiTypeResolver)));

				boolean annotationFound = false;
				// Detect if is a header variable
				final MergedAnnotation headerVariableMA = mergedAnnotations
					.get("org.springframework.web.bind.annotation.RequestHeader");
				if(headerVariableMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.HEADER);
					paramObj.setRequired(headerVariableMA.getBoolean("required") &&
						VALUE_CONSTANT_DEFAULT.equals(headerVariableMA.getString("defaultValue")));
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = headerVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger().debug("RequestHeader annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a path variable
				final MergedAnnotation pathVariableMA = mergedAnnotations
					.get("org.springframework.web.bind.annotation.PathVariable");
				if(pathVariableMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.PATH);
					paramObj.setRequired(pathVariableMA.getBoolean("required"));
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = pathVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger().debug("PathVariable annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a query variable
				final MergedAnnotation requestParamMA = mergedAnnotations
					.get("org.springframework.web.bind.annotation.RequestParam");
				if(requestParamMA.isPresent()) {
					annotationFound = true;
					if(paramObj.isMultipartFile()) {
						// MultipartFile parameters are considered as a requestBody)
						paramObj.setLocation(ParameterLocation.BODY);
					} else {
						paramObj.setLocation(ParameterLocation.QUERY);
					}
					paramObj.setRequired(requestParamMA.getBoolean("required") &&
						VALUE_CONSTANT_DEFAULT.equals(requestParamMA.getString("defaultValue")));

					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = requestParamMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger().debug(
						"RequestParam annotation detected (" + paramObj.getName() + "), location is "
							+ paramObj.getLocation().toString());
				}

				// Detect if is a request body parameter
				final MergedAnnotation requestBodyMA = mergedAnnotations
					.get("org.springframework.web.bind.annotation.RequestBody");
				if(requestBodyMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.BODY);
					paramObj.setRequired(requestBodyMA.getBoolean("required"));
					context.getLogger()
						.debug("RequestBody annotation detected, location is " + paramObj.getLocation().toString());
				}

				// Detect if is a request part parameter
				final MergedAnnotation requestPartMA = mergedAnnotations
					.get("org.springframework.web.bind.annotation.RequestPart");
				if(requestPartMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.BODY_PART);
					paramObj.setRequired(requestPartMA.getBoolean("required"));
					final String value = requestPartMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger()
						.debug("RequestPart annotation detected, location is " + paramObj.getLocation().toString());
				}

				if(!annotationFound) {
					// By default, some class are automatically resolved as @RequestParam for Spring
					// https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestparam.html

					if(isSpringSimpleProperty(parameter.getType())) {
						paramObj.setLocation(ParameterLocation.QUERY);
						paramObj.setRequired(true);
					}
				}

				// Class "requirement" has precedence on any annotation (we can't force an optional to be required ...)
				if(paramObj.getClassRequired() != null) {
					paramObj.setRequired(paramObj.getClassRequired());
				}

				// Add eventual extra information given by a swagger annotation
				this.setSwaggerAnnotatedParameterProperties(parameter, mergedAnnotations, paramObj);
			}
		}

		// Last case, some Dto fields can be bound to QueryParams : http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
		// Since this functionality is not well documented, it can be for now a subset of the complete functionality
		final Map<String, ParameterObject> unnestedParams = new LinkedHashMap<>(parameters);
		parameters.values().stream().filter(x -> x.getLocation() == null && parameterObjectBindableToQueryParams(x))
			.forEach(paramObj -> {
				bindDtoToQueryParams(unnestedParams, paramObj);
			});

		return unnestedParams.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
	}

	/**
	 * Reads parameters only present in the request mapping annotation (see spring RequestMapping#params)
	 *
	 * @param endpointAnnotations
	 * @param parameters
	 */
	private void readRequestMappingParams(MergedAnnotations endpointAnnotations, Map<String, ParameterObject> parameters) {
		// Extract from RequestMapping#params javadoc :
		// a sequence of "myParam=myValue" style expressions, with a request only mapped if each such parameter is found to have the given value.
		// Expressions can be negated by using the "!=" operator, as in "myParam!=myValue".
		// "myParam" style expressions are also supported, with such parameters having to be present in the request (allowed to have any value).
		// Finally, "!myParam" style expressions indicate that the specified parameter is not supposed to be present in the request.
		final MergedAnnotation requestMappingMergedAnnotation = endpointAnnotations
			.get("org.springframework.web.bind.annotation.RequestMapping");
		final String[] params = requestMappingMergedAnnotation.getStringArray("params");
		for(String param : params) {

			// We do not handle params starting with ! (ex: !myParam) since they must not be present in the request
			if(!param.startsWith("!")) {
				if(param.contains("=")) {
					if(!param.contains("!=")) {
						// myParam=myValue
						String[] array = param.split("=");
						if(array.length == 2) {
							ParameterObject po = new ParameterObject(array[0], Object.class, openApiTypeResolver);
							po.setLocation(ParameterLocation.QUERY);
							po.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
							parameters.put(array[0], po);
						}
					}
				} else {
					// Handles empty value params
					ParameterObject po = new ParameterObject(param, Object.class, openApiTypeResolver);
					po.setAllowEmptyValue(true);
					po.setLocation(ParameterLocation.QUERY);
					po.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
					parameters.put(param, po);
				}
			}
		}
	}

	/**
	 * Reads headers only present in the request mapping annotation (see spring RequestMapping#params)
	 *
	 * @param endpointAnnotations
	 * @param parameters
	 */
	private void readRequestMappingHeaders(MergedAnnotations endpointAnnotations, Map<String, ParameterObject> parameters) {
		// Extract from RequestMapping#headers javadoc :
		// a sequence of "myHeader=myValue" style expressions, with a request only mapped if each such parameter is found to have the given value.
		// Expressions can be negated by using the "!=" operator, as in "myHeader!=myValue".
		// "myParam" style expressions are also supported, with such parameters having to be present in the request (allowed to have any value).
		// Finally, "!myHeader" style expressions indicate that the specified parameter is not supposed to be present in the request.
		final MergedAnnotation requestMappingMergedAnnotation = endpointAnnotations
			.get("org.springframework.web.bind.annotation.RequestMapping");
		final String[] params = requestMappingMergedAnnotation.getStringArray("headers");
		for(String param : params) {

			// We do not handle params starting with ! (ex: !myHeader) since they must not be present in the request
			if(!param.startsWith("!")) {
				if(param.contains("=")) {
					if(!param.contains("!=")) {
						// myHeader=myValue
						String[] array = param.split("=");
						if(array.length == 2) {
							ParameterObject po = new ParameterObject(array[0], Object.class, openApiTypeResolver);
							po.setLocation(ParameterLocation.HEADER);
							po.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
							parameters.put(array[0], po);
						}
					}
				} else {
					// Handles empty value headers
					ParameterObject po = new ParameterObject(param, Object.class, openApiTypeResolver);
					po.setAllowEmptyValue(true);
					po.setLocation(ParameterLocation.HEADER);
					po.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
					parameters.put(param, po);
				}
			}
		}
	}

	private boolean parameterObjectBindableToQueryParams(final ParameterObject paramObj) {
		final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), paramObj.getJavaClass());
		for(final Field field : fields) {
			if(field.isAnnotationPresent(JsonIgnore.class)) {
				// Field is tagged ignore. No need to document it.
				continue;
			}
			if(!(isSimpleProperty(field.getType()) ||
				Collection.class.isAssignableFrom(field.getType()) ||
				field.getType().isArray())) {
				return false;
			}
		}
		return true;
	}

	private void bindDtoToQueryParams(final Map<String, ParameterObject> parameters, final ParameterObject paramObj) {

		final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), paramObj.getJavaClass());
		for(final Field field : fields) {
			final ParameterObject fieldObj = parameters.computeIfAbsent(field.getName(),
				(name) -> unwrapParameterObject(
					new ParameterObject(name, paramObj.getContextualType(field.getGenericType()), openApiTypeResolver)));
			fieldObj.setLocation(ParameterLocation.QUERY);
			fieldObj.setJavadocFieldClassName(paramObj.getJavaClass().getCanonicalName());
			// Class "requirement" has precedence on any annotation (we can't force an optional to be required ...)
			if(fieldObj.getClassRequired() != null) {
				fieldObj.setRequired(paramObj.getClassRequired());
			} else {
				if(context.getNullableConfiguration().hasNonNullAnnotation(Arrays.asList(field.getAnnotations()))) {
					fieldObj.setRequired(true);
				} else if(context.getNullableConfiguration().hasNullableAnnotation(Arrays.asList(field.getAnnotations()))) {
					fieldObj.setRequired(false);
				} else {
					fieldObj.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
				}
			}
		}
	}

	@Override
	protected List<String> readEndpointPaths(final String basePath,
		final MergedAnnotation requestMappingMergedAnnotation) {

		final String[] paths = requestMappingMergedAnnotation.getStringArray("path");
		final List<String> resolvedPaths = new ArrayList<>();
		if(paths.length == 0) {
			resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.getPathEnhancement()));
		}
		for(final String path : paths) {
			resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.getPathEnhancement()));
		}
		return resolvedPaths;
	}

	@Override
	protected void setConsumeProduceProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException {

		final MergedAnnotation requestMappingMergedAnnotation = mergedAnnotations
			.get("org.springframework.web.bind.annotation.RequestMapping");

		final Optional<ParameterObject> body = endpoint.getParameters().stream()
			.filter(x -> ParameterLocation.BODY == x.getLocation())
			.findAny();
		if(body.isPresent()) {
			final String[] consumes = requestMappingMergedAnnotation.getStringArray("consumes");
			if(consumes.length > 0) {
				body.get().setFormats(Arrays.asList(consumes));
			}
		}
		if(endpoint.getResponseObject() != null) {
			final String[] produces = requestMappingMergedAnnotation.getStringArray("produces");
			if(produces.length > 0) {
				endpoint.setResponseFormats(Arrays.asList(produces));
			}
		}
	}

	@Override
	protected int readResponseCode(final MergedAnnotations mergedAnnotations) {
		final MergedAnnotation responseStatusMA = mergedAnnotations.get("org.springframework.web.bind.annotation.ResponseStatus");
		if(!responseStatusMA.isPresent()) {
			return 200;
		}
		Object httpStatus = responseStatusMA.getValue("value").get();
		return httpStatusValue(httpStatus);
	}

	/**
	 * This function must act as close as the spring version :
	 * https://github.com/spring-projects/spring-framework/blob/main/spring-beans/src/main/java/org/springframework/beans/BeanUtils.java#L691
	 */
	private static boolean isSpringSimpleProperty(Class<?> type) {
		return isSimpleValueType(type) || (type.isArray() && isSimpleValueType(type.getComponentType()));
	}

	private static boolean isSimpleValueType(Class<?> type) {
		return (!(type == void.class || type == Void.class) &&
			((type.isPrimitive() || primitiveWrapperTypeMap.containsKey(type)) ||
				Enum.class.isAssignableFrom(type) ||
				CharSequence.class.isAssignableFrom(type) ||
				Number.class.isAssignableFrom(type) ||
				Date.class.isAssignableFrom(type) ||
				Temporal.class.isAssignableFrom(type) ||
				ZoneId.class.isAssignableFrom(type) ||
				TimeZone.class.isAssignableFrom(type) ||
				File.class.isAssignableFrom(type) ||
				Path.class.isAssignableFrom(type) ||
				Charset.class.isAssignableFrom(type) ||
				Currency.class.isAssignableFrom(type) ||
				InetAddress.class.isAssignableFrom(type) ||
				URI.class == type ||
				URL.class == type ||
				UUID.class == type ||
				Locale.class == type ||
				Pattern.class == type ||
				Class.class == type));
	}
}
