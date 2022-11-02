package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.CommonApiConfiguration;
import io.github.kbuntrock.configuration.library.JaxrsHttpVerb;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.OperationType;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * Analyse a java class in light of an api configuration object
 */
public class JavaClassAnalyser {

	private static final Pattern BEGINNING = Pattern.compile("^[a-z\\.]*");
	private static final Pattern FIRST_GENERIC = Pattern.compile("<[a-z\\.]*");
	private static final Pattern OTHER_GENERIC = Pattern.compile(",[a-z\\.]*");

	private final Log logger = Logger.INSTANCE.getLogger();

	private final ApiConfiguration apiConfiguration;

	private final List<Pair<Pattern, Pattern>> whiteListPatterns = new ArrayList<>();
	private final List<Pair<Pattern, Pattern>> blackListPatterns = new ArrayList<>();

	public JavaClassAnalyser(final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;

		// Compilation of white list / black list patterns
		if(apiConfiguration.getWhiteList() != null) {
			for(final String whiteEntry : apiConfiguration.getWhiteList()) {
				final String[] regexArray = whiteEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 2) {
					if(StringUtils.isEmpty(regexArray[0])) {
						whiteListPatterns.add(Pair.of(null, Pattern.compile(regexArray[1])));
					} else {
						whiteListPatterns.add(Pair.of(Pattern.compile(regexArray[0]), Pattern.compile(regexArray[1])));
					}
				}
			}
		}
		if(apiConfiguration.getBlackList() != null) {
			for(final String blackEntry : apiConfiguration.getBlackList()) {
				final String[] regexArray = blackEntry.split(CommonApiConfiguration.SEPARATOR_CLASS_METHOD);
				if(regexArray.length == 2) {
					if(StringUtils.isEmpty(regexArray[0])) {
						blackListPatterns.add(Pair.of(null, Pattern.compile(regexArray[1])));
					} else {
						blackListPatterns.add(Pair.of(Pattern.compile(regexArray[0]), Pattern.compile(regexArray[1])));
					}
				}
			}
		}
	}

	private static String createTypeIdentifier(final String typeName) {

		String returnTypeName = typeName;
		final Matcher matcher = BEGINNING.matcher(returnTypeName);
		if(matcher.find() && matcher.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher.group(), "");
		}
		final Matcher matcher2 = FIRST_GENERIC.matcher(returnTypeName);
		if(matcher2.find() && matcher2.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher2.group(), "<");
		}
		final Matcher matcher3 = OTHER_GENERIC.matcher(returnTypeName);
		if(matcher3.find() && matcher3.group().contains(".")) {
			returnTypeName = returnTypeName.replace(matcher3.group(), ",");
		}

		return returnTypeName;
	}

	public static String createIdentifier(final Method method) {
		final StringBuilder sb = new StringBuilder();
		sb.append(createTypeIdentifier(method.getGenericReturnType().getTypeName()));
		sb.append("_");
		sb.append(method.getName());
		for(final Parameter parameter : method.getParameters()) {
			sb.append("_");
			sb.append(createTypeIdentifier(parameter.getParameterizedType().getTypeName()));
		}
		return sb.toString();
	}

	private static int readSpringResponseCode(final MergedAnnotations mergedAnnotations) {

		final MergedAnnotation<ResponseStatus> responseStatusMA = mergedAnnotations.get(ResponseStatus.class);
		if(!responseStatusMA.isPresent()) {
			return HttpStatus.OK.value();
		}
		return responseStatusMA.getValue("value", HttpStatus.class).get().value();
	}

	/**
	 * Set the consume and produce properties of an endpoint
	 *
	 * @param endpoint                       the endpoint object to set
	 * @param requestMappingMergedAnnotation
	 */
	private static void setSpringConsumeProduceProperties(final Endpoint endpoint,
		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) throws MojoFailureException {

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

	private static void setJaxrsConsumeProduceProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException {

		final MergedAnnotation<Consumes> consumesMergedAnnotation = mergedAnnotations.get(Consumes.class);
		final MergedAnnotation<Produces> producesMergedAnnotation = mergedAnnotations.get(Produces.class);

		final Optional<ParameterObject> body = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation())
			.findAny();
		if(body.isPresent() && consumesMergedAnnotation.isPresent()) {
			final String[] consumes = consumesMergedAnnotation.getStringArray("value");
			if(consumes.length > 0) {
				body.get().setFormats(Arrays.asList(consumes));
			}
		}
		if(endpoint.getResponseObject() != null && producesMergedAnnotation.isPresent()) {
			final String[] produces = producesMergedAnnotation.getStringArray("value");
			if(produces.length > 0) {
				endpoint.setResponseFormats(Arrays.asList(produces));
			}
		}
	}

	private static String concatenateBasePathAndMethodPath(final String basePath, final String methodPath,
		final boolean automaticSeparator) {
		String result = basePath + methodPath;
		if(automaticSeparator) {
			if(!methodPath.isEmpty() && !methodPath.startsWith("/") && !basePath.endsWith("/")) {
				result = basePath + "/" + methodPath;
			}
			if(!result.startsWith("/")) {
				result = "/" + result;
			}
		}
		return result;
	}

	/**
	 * Create a Tag from a java class containing REST mapping functions
	 *
	 * @param clazz a REST controller class
	 * @return an tag (if there is at least one declared endpoint)
	 * @throws MojoFailureException
	 */
	public Optional<Tag> getTagFromClass(final Class<?> clazz) throws MojoFailureException {
		final Tag tag = new Tag(clazz);
		logger.debug("Parsing tag : " + tag.getName());
		List<String> basePaths = Collections.singletonList("");

		final MergedAnnotations mergedAnnotations = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

		if(Library.SPRING_MVC == apiConfiguration.getLibrary()) {
			// Spring MVC version
			final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
			if(requestMappingMergedAnnotation.isPresent()) {
				final String[] paths = requestMappingMergedAnnotation.getStringArray("value");
				if(paths.length > 0) {
					basePaths = Arrays.asList(paths);
				}
			}
		} else if(Library.JAXRS == apiConfiguration.getLibrary()) {
			// JAXRS version
			final MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
			if(requestMappingMergedAnnotation.isPresent()) {
				final String path = requestMappingMergedAnnotation.getString("value");
				if(!StringUtils.isEmpty(path)) {
					basePaths = Collections.singletonList(path);
				}
			}
		}

		for(final String basePath : basePaths) {
			parseEndpoints(tag, basePath, clazz);
		}

		if(tag.getEndpoints().isEmpty()) {
			// There was not valid endpoint to attach to this tag. Therefore, we don't keep track of it.
			return Optional.empty();
		} else {
			return Optional.of(tag);
		}

	}

	private void parseEndpoints(final Tag tag, final String basePath, final Class<?> clazz) throws MojoFailureException {

		logger.debug("Parsing endpoint " + clazz.getSimpleName());

		final Method[] methods = clazz.getMethods();

		for(final Method method : methods) {

			if(validateWhiteList(clazz, method) && validateBlackList(clazz, method)) {
				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
				if(Library.SPRING_MVC == apiConfiguration.getLibrary()) {
					computeSpringAnnotations(basePath, method, mergedAnnotations, tag);
				} else { // Jaxrs scanning
					computeJaxrsAnnotations(basePath, method, mergedAnnotations, tag);
				}
			}
		}
	}

	private void computeSpringAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations,
		final Tag tag) throws MojoFailureException {
		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
		if(requestMappingMergedAnnotation.isPresent()) {
			final RequestMethod[] requestMethods = requestMappingMergedAnnotation.getEnumArray("method", RequestMethod.class);
			if(requestMethods.length > 0) {
				logger.debug("Parsing request method : " + method.getName());
				final String methodIdentifier = createIdentifier(method);
				final List<ParameterObject> parameterObjects = readSpringParameters(method);
				final DataObject responseObject = readResponseObject(method);
				final int responseCode = readSpringResponseCode(mergedAnnotations);
				final List<String> paths = readSpringEndpointPaths(basePath, requestMappingMergedAnnotation);
				for(final RequestMethod requestMethod : requestMethods) {
					for(final String path : paths) {
						final Endpoint endpoint = new Endpoint();
						endpoint.setType(OperationType.from(requestMethod));
						endpoint.setPath(path);
						endpoint.setName(method.getName());
						endpoint.setParameters(parameterObjects);
						endpoint.setResponseObject(responseObject);
						endpoint.setResponseCode(responseCode);
						setSpringConsumeProduceProperties(endpoint, requestMappingMergedAnnotation);
						endpoint.setIdentifier(methodIdentifier);
						endpoint.setDeprecated(isDeprecated(method));
						tag.addEndpoint(endpoint);
						logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
					}
				}
			}
		}
	}

	private void computeJaxrsAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations,
		final Tag tag) throws MojoFailureException {
		final MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
		if(requestMappingMergedAnnotation.isPresent()) {

			for(final JaxrsHttpVerb verb : JaxrsHttpVerb.values()) {
				final MergedAnnotation m = mergedAnnotations.get(verb.getAnnotationClass());
				if(m.isPresent()) {
					final String methodIdentifier = createIdentifier(method);
					final List<ParameterObject> parameterObjects = readJaxrsParameters(method);
					final DataObject responseObject = readResponseObject(method);
					final int responseCode = HttpStatus.OK.value();
					readJaxrsEndpointPaths(basePath, requestMappingMergedAnnotation);
					final String path = readJaxrsEndpointPaths(basePath, requestMappingMergedAnnotation);
					final Endpoint endpoint = new Endpoint();
					endpoint.setType(OperationType.from(verb.getAnnotationClass()));
					endpoint.setPath(path);
					endpoint.setName(method.getName());
					endpoint.setParameters(parameterObjects);
					endpoint.setResponseObject(responseObject);
					endpoint.setResponseCode(responseCode);
					setJaxrsConsumeProduceProperties(endpoint, mergedAnnotations);
					endpoint.setIdentifier(methodIdentifier);
					endpoint.setDeprecated(isDeprecated(method));
					tag.addEndpoint(endpoint);
					logger.debug("Finished parsing endpoint : " + endpoint.getName() + " - " + endpoint.getType().name());
				}
			}
		}
	}

	private boolean isDeprecated(final Method originalMethod) {
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);
		for(final Method method : overridenMethods) {
			if(method.getDeclaredAnnotation(Deprecated.class) != null) {
				return true;
			}
		}
		return false;
	}

	private List<ParameterObject> readSpringParameters(final Method originalMethod) {

		logger.debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {
			for(final Parameter parameter : method.getParameters()) {
				if(HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
					continue;
				}
				logger.debug("Parameter : " + parameter.getName());

				final ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
					(name) -> new ParameterObject(name, parameter.getParameterizedType()));

				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter,
					MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

				// Detect if is a path variable
				final MergedAnnotation<PathVariable> pathVariableMA = mergedAnnotations.get(PathVariable.class);
				if(pathVariableMA.isPresent()) {
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

					final boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
						(OpenApiDataType.ARRAY == paramObj.getOpenApiType() && MultipartFile.class == paramObj.getArrayItemDataObject()
							.getJavaClass());
					if(isMultipartFile) {
						// MultipartFile parameters are considered as a requestBody)
						paramObj.setLocation(ParameterLocation.BODY);
					} else {
						paramObj.setLocation(ParameterLocation.QUERY);
					}
					paramObj.setRequired(requestParamMA.getBoolean("required"));

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
					paramObj.setLocation(ParameterLocation.BODY);
					paramObj.setRequired(requestBodyMA.getBoolean("required"));
					logger.debug("RequestBody annotation detected, location is " + paramObj.getLocation().toString());
				}

			}
		}

		return parameters.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
	}

	private List<ParameterObject> readJaxrsParameters(final Method originalMethod) {

		logger.debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {

			boolean bodyParameterDetected = false;

			for(final Parameter parameter : method.getParameters()) {
				if(HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
					continue;
				}
				logger.debug("Parameter : " + parameter.getName());

				final ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
					(name) -> new ParameterObject(name, parameter.getParameterizedType()));

				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter,
					MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

				final MergedAnnotation<NotNull> notnullMA = mergedAnnotations.get(NotNull.class);
				// Detect if required
				paramObj.setRequired(notnullMA.isPresent());

				// Detect if is a path variable
				final MergedAnnotation<PathParam> pathVariableMA = mergedAnnotations.get(PathParam.class);
				if(pathVariableMA.isPresent()) {
					paramObj.setLocation(ParameterLocation.PATH);
					// Path params are required
					paramObj.setRequired(true);
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = pathVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					logger.debug("PathParam annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a query variable
				final MergedAnnotation<QueryParam> requestParamMA = mergedAnnotations.get(QueryParam.class);
				if(requestParamMA.isPresent()) {

					final boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
						(OpenApiDataType.ARRAY == paramObj.getOpenApiType() && MultipartFile.class == paramObj.getArrayItemDataObject()
							.getJavaClass());
					if(isMultipartFile) {
						// MultipartFile parameters are considered as a requestBody)
						paramObj.setLocation(ParameterLocation.BODY);
					} else {
						paramObj.setLocation(ParameterLocation.QUERY);
					}

					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = requestParamMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					logger.debug(
						"QueryParam annotation detected (" + paramObj.getName() + "), location is " + paramObj.getLocation().toString());
				}

				// Detect if is a request body parameter (if it is not a path or a query param)
				if(paramObj.getLocation() == null) {
					if(bodyParameterDetected) {
						bodyParameterDetected = true;
						logger.error("Cannot set multiple body parameters, (" + paramObj.getName() + ")");
					} else {
						paramObj.setLocation(ParameterLocation.BODY);
						logger.debug(
							"Body parameter detected (" + paramObj.getName() + "), location is " + paramObj.getLocation().toString());
					}
				}

			}
		}

		return parameters.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
	}

	private DataObject readResponseObject(final Method method) {
		final Class<?> returnType = method.getReturnType();
		if(Void.class == returnType || Void.TYPE == returnType) {
			return null;
		}
		DataObject dataObject = new DataObject(method.getGenericReturnType());
		dataObject = computeFrameworkReturnObject(dataObject);
		logger.debug(dataObject.toString());
		return dataObject;
	}

	/**
	 * Some returned objects are handled in a specific manner by spring.
	 * In that case, we have to adapt it
	 *
	 * @param dataObject source
	 * @return return DataObject
	 */
	private DataObject computeFrameworkReturnObject(final DataObject dataObject) {
		if(Optional.class.isAssignableFrom(dataObject.getJavaClass()) ||
			HttpEntity.class.isAssignableFrom(dataObject.getJavaClass())) {
			return new DataObject(dataObject.getGenericNameToTypeMap().get("T"));
		}
		return dataObject;
	}

	private List<String> readSpringEndpointPaths(final String basePath,
		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation) {
		final String[] paths = requestMappingMergedAnnotation.getStringArray("path");
		final List<String> resolvedPaths = new ArrayList<>();
		if(paths.length == 0) {
			resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement()));
		}
		for(final String path : paths) {
			resolvedPaths.add(concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.isSpringPathEnhancement()));
		}
		return resolvedPaths;
	}

	private String readJaxrsEndpointPaths(final String basePath, final MergedAnnotation<Path> requestMappingMergedAnnotation) {
		final String path = requestMappingMergedAnnotation.getString("value");
		if(path == null) {
			return concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.isSpringPathEnhancement());
		}
		return concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.isSpringPathEnhancement());
	}

	private boolean validateWhiteList(final Class<?> clazz, final Method method) {

		if(!whiteListPatterns.isEmpty()) {
			for(final Pair<Pattern, Pattern> pair : whiteListPatterns) {
				boolean validateLeft = true;
				if(pair.getLeft() != null) {
					validateLeft = pair.getLeft().matcher(clazz.getCanonicalName()).matches();
				}
				if(validateLeft && pair.getRight().matcher(method.getName()).matches()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	private boolean validateBlackList(final Class<?> clazz, final Method method) {
		if(!blackListPatterns.isEmpty()) {
			for(final Pair<Pattern, Pattern> pair : blackListPatterns) {
				boolean validateLeft = true;
				if(pair.getLeft() != null) {
					validateLeft = pair.getLeft().matcher(clazz.getCanonicalName()).matches();
				}
				if(validateLeft && pair.getRight().matcher(method.getName()).matches()) {
					return false;
				}
			}
		}
		return true;
	}

}
