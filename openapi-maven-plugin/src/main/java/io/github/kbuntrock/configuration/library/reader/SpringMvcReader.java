package io.github.kbuntrock.configuration.library.reader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.NullableConfigurationHolder;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.OperationType;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ClassGenericityResolver;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.multipart.MultipartFile;

public class SpringMvcReader extends AstractLibraryReader {

	public SpringMvcReader(final ApiConfiguration apiConfiguration) {
		super(apiConfiguration);
	}

	@Override
	public List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations) {
		List<String> basePaths = Collections.singletonList("");
		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
		if(requestMappingMergedAnnotation.isPresent()) {
			final String[] paths = requestMappingMergedAnnotation.getStringArray("value");
			if(paths.length > 0) {
				basePaths = Arrays.asList(paths);
			}
		}
		return basePaths;
	}

	@Override
	public void computeAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag,
		final ClassGenericityResolver genericityResolver) throws MojoFailureException {

		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
		if(requestMappingMergedAnnotation.isPresent() && !excludedByReturnType(method)) {

			genericityResolver.initForMethod(method);

			final RequestMethod[] requestMethods = requestMappingMergedAnnotation.getEnumArray("method", RequestMethod.class);
			if(requestMethods.length > 0) {
				logger.debug("Parsing request method : " + method.getName());
				final String methodIdentifier = JavaClassAnalyser.createIdentifier(method);
				final List<ParameterObject> parameterObjects = readParameters(method, genericityResolver);
				final DataObject responseObject = readResponseObject(method, genericityResolver, mergedAnnotations);
				final int responseCode = readResponseCode(mergedAnnotations);
				final List<String> paths = readEndpointPaths(basePath, requestMappingMergedAnnotation);
				for(final RequestMethod requestMethod : requestMethods) {
					for(final String path : paths) {
						final Endpoint endpoint = new Endpoint();
						endpoint.setType(OperationType.fromJavax(requestMethod));
						endpoint.setPath(path);
						endpoint.setName(method.getName());
						endpoint.setParameters(parameterObjects);
						endpoint.setResponseObject(responseObject);
						endpoint.setResponseCode(responseCode);
						setConsumeProduceProperties(endpoint, mergedAnnotations);
						endpoint.setIdentifier(methodIdentifier);
						endpoint.setDeprecated(isDeprecated(method));
						tag.addEndpoint(endpoint);
						logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
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
	 * @param originalMethod     inspected method
	 * @param genericityResolver genericity resolver
	 * @return list of parameters to document
	 */
	@Override
	protected List<ParameterObject> readParameters(final Method originalMethod, final ClassGenericityResolver genericityResolver) {
		logger.debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {
			for(final Parameter parameter : method.getParameters()) {

				if(!OpenApiTypeResolver.INSTANCE.canBeDocumented(parameter)) {
					continue;
				}
				logger.debug("Parameter : " + parameter.getName());

				final ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
					(name) -> unwrapParameterObject(
						new ParameterObject(name, genericityResolver.getContextualType(parameter.getParameterizedType(), method))));

				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter,
					MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

				boolean annotationFound = false;

				// Detect if is a header variable
				final MergedAnnotation<RequestHeader> headerVariableMA = mergedAnnotations.get(RequestHeader.class);
				if(headerVariableMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.HEADER);
					paramObj.setRequired(headerVariableMA.getBoolean("required") &&
						ValueConstants.DEFAULT_NONE.equals(headerVariableMA.getString("defaultValue")));
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = headerVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					logger.debug("RequestHeader annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a path variable
				final MergedAnnotation<PathVariable> pathVariableMA = mergedAnnotations.get(PathVariable.class);
				if(pathVariableMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.PATH);
					paramObj.setRequired(pathVariableMA.getBoolean("required"));
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = pathVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					logger.debug("PathVariable annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a query variable
				final MergedAnnotation<RequestParam> requestParamMA = mergedAnnotations.get(RequestParam.class);
				if(requestParamMA.isPresent()) {
					annotationFound = true;
					final boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
						(OpenApiDataType.ARRAY == paramObj.getOpenApiResolvedType().getType()
							&& MultipartFile.class == paramObj.getArrayItemDataObject().getJavaClass());
					if(isMultipartFile) {
						// MultipartFile parameters are considered as a requestBody)
						paramObj.setLocation(ParameterLocation.BODY);
					} else {
						paramObj.setLocation(ParameterLocation.QUERY);
					}
					paramObj.setRequired(requestParamMA.getBoolean("required") &&
						ValueConstants.DEFAULT_NONE.equals(requestParamMA.getString("defaultValue")));

					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = requestParamMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					logger.debug(
						"RequestParam annotation detected (" + paramObj.getName() + "), location is " + paramObj.getLocation().toString());
				}

				// Detect if is a request body parameter
				final MergedAnnotation<RequestBody> requestBodyMA = mergedAnnotations.get(RequestBody.class);
				if(requestBodyMA.isPresent()) {
					annotationFound = true;
					paramObj.setLocation(ParameterLocation.BODY);
					paramObj.setRequired(requestBodyMA.getBoolean("required"));
					logger.debug("RequestBody annotation detected, location is " + paramObj.getLocation().toString());
				}

				if(!annotationFound) {
					// By default, some class are automatically resolved as @RequestParam for Spring
					// https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestparam.html

					if(BeanUtils.isSimpleProperty(parameter.getType())) {
						paramObj.setLocation(ParameterLocation.QUERY);
						paramObj.setRequired(true);
					}
				}

				// Class "requirement" has precedence on any annotation (we can't force an optional to be required ...)
				if(paramObj.getClassRequired() != null) {
					paramObj.setRequired(paramObj.getClassRequired());
				}

			}
		}

		// Last case, some Dto fields can be binded to QueryParams : http://dolszewski.com/spring/how-to-bind-requestparam-to-object/
		// Since this functionality is not well documented, it can be for now a subset of the complete functionality
		final Map<String, ParameterObject> unnestedParams = new LinkedHashMap<>(parameters);
		parameters.values().stream().filter(x -> x.getLocation() == null && parameterObjectBindableToQueryParams(x)).forEach(paramObj -> {
			bindDtoToQueryParams(unnestedParams, paramObj);
		});

		return unnestedParams.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
	}

	private static boolean parameterObjectBindableToQueryParams(final ParameterObject paramObj) {
		final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), paramObj.getJavaClass());
		for(final Field field : fields) {
			if(field.isAnnotationPresent(JsonIgnore.class)) {
				// Field is tagged ignore. No need to document it.
				continue;
			}
			if(!(BeanUtils.isSimpleProperty(field.getType()) ||
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
					new ParameterObject(name, paramObj.getContextualType(field.getGenericType()))));
			fieldObj.setLocation(ParameterLocation.QUERY);
			fieldObj.setJavadocFieldClassName(paramObj.getJavaClass().getCanonicalName());
			// Class "requirement" has precedence on any annotation (we can't force an optional to be required ...)
			if(fieldObj.getClassRequired() != null) {
				fieldObj.setRequired(paramObj.getClassRequired());
			} else {
				if(NullableConfigurationHolder.hasNonNullAnnotation(Arrays.asList(field.getAnnotations()))) {
					fieldObj.setRequired(true);
				} else if(NullableConfigurationHolder.hasNullableAnnotation(Arrays.asList(field.getAnnotations()))) {
					fieldObj.setRequired(false);
				} else {
					fieldObj.setRequired(NullableConfigurationHolder.isDefaultNonNullableFields());
				}
			}
		}
	}

	@Override
	protected List<String> readEndpointPaths(final String basePath,
		final MergedAnnotation<? extends Annotation> requestMappingMergedAnnotation) {

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

		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);

		final Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation())
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
		final MergedAnnotation<ResponseStatus> responseStatusMA = mergedAnnotations.get(ResponseStatus.class);
		if(!responseStatusMA.isPresent()) {
			return HttpStatus.OK.value();
		}
		return responseStatusMA.getValue("value", HttpStatus.class).get().value();
	}
}
