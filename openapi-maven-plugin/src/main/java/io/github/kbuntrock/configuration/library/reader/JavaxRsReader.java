package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.OperationType;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ClassGenericityResolver;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
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
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class JavaxRsReader extends AstractLibraryReader {

	private Class jakartaNotNull;
	private Class jakartaHttpServletRequest;
	private Class responseAnnotation;

	public JavaxRsReader(final ApiConfiguration apiConfiguration) {
		super(apiConfiguration);
		try {
			// For the validation constraint, there should be no problem if the dependency is not present.
			jakartaNotNull = ClassLoaderUtils.getByName(JakartaRsReader.NOT_NULL_CNAME);
		} catch(final ClassNotFoundException e) {
			// Nothing to do, could be normal since it is in the validation api
		}
		try {
			jakartaHttpServletRequest = ClassLoaderUtils.getByName(JakartaRsReader.HttpServletRequest_CNAME);
		} catch(final ClassNotFoundException e) {
			// Nothing to do, could be normal since it is in the servlet api
		}
		initCustomResponseAnnotation(apiConfiguration);
	}

	private void initCustomResponseAnnotation(final ApiConfiguration apiConfiguration) {

		if(apiConfiguration.getCustomResponseTypeAnnotation() != null) {
			final String annotationName = apiConfiguration.getCustomResponseTypeAnnotation();
			try {
				responseAnnotation = ClassLoaderUtils.getByName(annotationName);
				try {
					final Method responseAnnotationMethod = responseAnnotation.getMethod("value");
					if(responseAnnotationMethod.getReturnType() != Class.class) {
						throw new RuntimeException("Annotation " + annotationName + " does not declare a method called value()");
					}
				} catch(final NoSuchMethodException e) {
					throw new RuntimeException("Annotation " + annotationName + " does not declare a method value() returning a Class");
				}
			} catch(final ClassNotFoundException e) {
				throw new RuntimeException("Could not load annotation class " + annotationName);
			}

		}
	}

	@Override
	public List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations) {
		List<String> basePaths = Collections.singletonList("");
		final MergedAnnotation<Annotation> requestMappingMergedAnnotation = mergedAnnotations.get("javax.ws.rs.Path");
		if(requestMappingMergedAnnotation.isPresent()) {
			final String path = requestMappingMergedAnnotation.getString("value");
			if(!StringUtils.isEmpty(path)) {
				basePaths = Collections.singletonList(path);
			}
		}
		return basePaths;
	}

	@Override
	public void computeAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag,
		final ClassGenericityResolver genericityResolver) throws MojoFailureException {

		final MergedAnnotation<Annotation> requestMappingMergedAnnotation = mergedAnnotations.get("javax.ws.rs.Path");
		if(requestMappingMergedAnnotation.isPresent()) {

			genericityResolver.initForMethod(method);

			for(final JavaxRsHttpVerb verb : JavaxRsHttpVerb.values()) {
				final MergedAnnotation<Annotation> m = mergedAnnotations.get(verb.getAnnotationClass());
				if(m.isPresent()) {
					final String methodIdentifier = JavaClassAnalyser.createIdentifier(method);
					final List<ParameterObject> parameterObjects = readParameters(method, genericityResolver);
					final DataObject responseObject = readResponseObject(method, genericityResolver, mergedAnnotations);
					final int responseCode = readResponseCode(null);
					final String path = readEndpointPaths(basePath, requestMappingMergedAnnotation).get(0);
					final Endpoint endpoint = new Endpoint();
					endpoint.setType(OperationType.fromJavax(verb.getAnnotationClass()));
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

	@Override
	protected List<ParameterObject> readParameters(final Method originalMethod, final ClassGenericityResolver genericityResolver) {
		logger.debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {

			boolean bodyParameterDetected = false;

			for(final Parameter parameter : method.getParameters()) {

				if(!OpenApiTypeResolver.INSTANCE.canBeDocumented(parameter)) {
					continue;
				}
				logger.debug("Parameter : " + parameter.getName());

				ParameterObject paramObj = new ParameterObject(parameter.getName(),
					genericityResolver.getContextualType(parameter.getParameterizedType(), method));
				paramObj = unwrapParameterObject(paramObj);

				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter,
					MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

				if(mergedAnnotations.get("javax.ws.rs.BeanParam").isPresent()) {
					continue;
				}
				parameters.putIfAbsent(paramObj.getName(), paramObj);

				final MergedAnnotation<Annotation> notnullMA = mergedAnnotations.get("javax.validation.constraints.NotNull");
				// Detect if required
				if(notnullMA.isPresent()) {
					paramObj.setRequired(notnullMA.isPresent());
				} else if(jakartaNotNull != null) {
					paramObj.setRequired(mergedAnnotations.get(jakartaNotNull).isPresent());
				}

				// Detect if is a path variable
				final MergedAnnotation<Annotation> pathVariableMA = mergedAnnotations.get("javax.ws.rs.PathParam");
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
				final MergedAnnotation<Annotation> requestParamMA = mergedAnnotations.get("javax.ws.rs.QueryParam");
				if(requestParamMA.isPresent()) {

					final boolean isMultipartFile = MultipartFile.class == paramObj.getJavaClass() ||
						(OpenApiDataType.ARRAY == paramObj.getOpenApiResolvedType().getType()
							&& MultipartFile.class == paramObj.getArrayItemDataObject().getJavaClass());
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

	@Override
	protected List<String> readEndpointPaths(final String basePath, final MergedAnnotation<? extends Annotation> pathMergedAnnotation) {
		final String path = pathMergedAnnotation.getString("value");
		if(path == null) {
			return Arrays.asList(concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.getPathEnhancement()));
		}
		return Arrays.asList(concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.getPathEnhancement()));
	}

	@Override
	protected void setConsumeProduceProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException {
		final MergedAnnotation<Annotation> consumesMergedAnnotation = mergedAnnotations.get("javax.ws.rs.Consumes");
		final MergedAnnotation<Annotation> producesMergedAnnotation = mergedAnnotations.get("javax.ws.rs.Produces");

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

	@Override
	protected int readResponseCode(final MergedAnnotations mergedAnnotations) {
		return HttpStatus.OK.value();
	}

	private enum JavaxRsHttpVerb {
		GET("javax.ws.rs.GET"),
		PUT("javax.ws.rs.PUT"),
		POST("javax.ws.rs.POST"),
		DELETE("javax.ws.rs.DELETE"),
		PATCH("javax.ws.rs.PATCH"),
		OPTIONS("javax.ws.rs.OPTIONS"),
		HEAD("javax.ws.rs.HEAD");

		private final String annotationClass;

		JavaxRsHttpVerb(final String annotationClass) {
			this.annotationClass = annotationClass;
		}

		public String getAnnotationClass() {
			return annotationClass;
		}
	}

	@Override
	protected Type readResponseMethodType(final Method method, final MergedAnnotations mergedAnnotations) {
		if(responseAnnotation != null && mergedAnnotations.isPresent(responseAnnotation)) {
			return (Class) mergedAnnotations.get(responseAnnotation).getValue("value").get();
		}
		return method.getGenericReturnType();
	}
}
