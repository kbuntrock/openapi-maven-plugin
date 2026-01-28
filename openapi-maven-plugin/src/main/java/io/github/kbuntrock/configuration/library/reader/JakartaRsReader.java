package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.model.*;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.ParameterLocation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class JakartaRsReader extends AstractLibraryReader {

	private static final String PATH_CNAME = "jakarta.ws.rs.Path";
	public static final String NOT_NULL_CNAME = "jakarta.validation.constraints.NotNull";
	public static final String BEAN_PARAM_CNAME = "jakarta.ws.rs.BeanParam";
	public static final String HttpServletRequest_CNAME = "jakarta.servlet.http.HttpServletRequest";
	private Class jakartaPath;
	private Class jakartaNotNull;
	private Class jakartaBeanParam;
	private Class jakartaHttpServletRequest;
	private Class responseAnnotation;

	public JakartaRsReader(final ApiContext context, final ApiConfiguration apiConfiguration,
		final OpenApiTypeResolver openApiTypeResolver) {
		super(context, apiConfiguration, openApiTypeResolver);
		initClasses();
	}

	private void initClasses() {
		// If the jakarta path class is not present, there is a configuration error
		jakartaPath = context.getClassLoaderHelper().getByNameRuntimeEx(PATH_CNAME);
		// The BeanParam class is in the same jar than the Path annotation
		jakartaBeanParam = context.getClassLoaderHelper().getByNameRuntimeEx(BEAN_PARAM_CNAME);
		try {
			// For the validation constraint, there should be no problem if the dependency is not present.
			jakartaNotNull = context.getClassLoaderHelper().getByName(NOT_NULL_CNAME);
		} catch(final ClassNotFoundException e) {
			// Nothing to do, could be normal since it is in the validation api
		}
		try {
			jakartaHttpServletRequest = context.getClassLoaderHelper().getByName(HttpServletRequest_CNAME);
		} catch(final ClassNotFoundException e) {
			// Nothing to do, could be normal since it is in the servlet api
		}
		initCustomResponseAnnotation(apiConfiguration);
	}

	private void initCustomResponseAnnotation(final ApiConfiguration apiConfiguration) {

		if(apiConfiguration.getCustomResponseTypeAnnotation() != null) {
			final String annotationName = apiConfiguration.getCustomResponseTypeAnnotation();
			try {
				responseAnnotation = context.getClassLoaderHelper().getByName(annotationName);
				try {
					final Method responseAnnotationMethod = responseAnnotation.getMethod("value");
					if(responseAnnotationMethod.getReturnType() != Class.class) {
						throw new RuntimeException("Annotation " + annotationName + " does not declare a method called value()");
					}
				} catch(final NoSuchMethodException e) {
					throw new RuntimeException(
						"Annotation " + annotationName + " does not declare a method value() returning a Class");
				}
			} catch(final ClassNotFoundException e) {
				throw new RuntimeException("Could not load annotation class " + annotationName);
			}

		}
	}

	@Override
	public List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations) {
		List<String> basePaths = Collections.singletonList("");
		final MergedAnnotation requestMappingMergedAnnotation = mergedAnnotations.get(PATH_CNAME);
		if(requestMappingMergedAnnotation.isPresent()) {
			final String path = requestMappingMergedAnnotation.getString("value");
			if(!StringUtils.isEmpty(path)) {
				basePaths = Collections.singletonList(path);
			}
		}
		return basePaths;
	}

	@Override
	public void computeAnnotations(final Class clazz, final String basePath, final Method method,
		final MergedAnnotations mergedAnnotations, final Tag tag) throws MojoFailureException {

		final MergedAnnotation requestMappingMergedAnnotation = mergedAnnotations.get(PATH_CNAME);
		if(requestMappingMergedAnnotation.isPresent()) {

			for(final JakartaRsHttpVerb verb : JakartaRsHttpVerb.values()) {
				final MergedAnnotation m = mergedAnnotations.get(verb.getAnnotationClassName());
				if(m.isPresent()) {
					final String methodIdentifier = JavaClassAnalyser.createMethodIdentifier(method);
					final List<ParameterObject> parameterObjects = readParameters(clazz, method, mergedAnnotations);
					final DataObject responseObject = readResponseObject(clazz, method, mergedAnnotations);
					final int responseCode = readResponseCode(null);
					final String path = readEndpointPaths(basePath, requestMappingMergedAnnotation).get(0);
					final Endpoint endpoint = new Endpoint();
					endpoint.setType(
						OperationType.fromJakarta(verb.getAnnotationClass(context.getClassLoaderHelper()).getCanonicalName()));
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

	@Override
	protected List<ParameterObject> readParameters(final Class clazz, final Method originalMethod,
		final MergedAnnotations endpointAnnotations) {
		context.getLogger().debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {

			boolean bodyParameterDetected = false;

			for(final Parameter parameter : method.getParameters()) {

				final MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper().from(parameter);

				if(!openApiTypeResolver.canBeDocumented(parameter, mergedAnnotations)) {
					continue;
				}
				context.getLogger().debug("Parameter : " + parameter.getName());

				ParameterObject paramObj = new ParameterObject(parameter.getName(),
					genericityResolver.resolve(clazz, parameter.getParameterizedType()), openApiTypeResolver);
				paramObj = unwrapParameterObject(paramObj);

				if(mergedAnnotations.get(BEAN_PARAM_CNAME).isPresent()) {
					continue;
				}
				parameters.putIfAbsent(paramObj.getName(), paramObj);

				final MergedAnnotation notnullMA = mergedAnnotations.get("javax.validation.constraints.NotNull");
				// Detect if required
				if(notnullMA.isPresent()) {
					paramObj.setRequired(notnullMA.isPresent());
				} else if(jakartaNotNull != null) {
					paramObj.setRequired(mergedAnnotations.get(NOT_NULL_CNAME).isPresent());
				}

				// Detect if is a path variable
				final MergedAnnotation pathVariableMA = mergedAnnotations.get("jakarta.ws.rs.PathParam");
				if(pathVariableMA.isPresent()) {
					paramObj.setLocation(ParameterLocation.PATH);
					// Path params are required
					paramObj.setRequired(true);
					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = pathVariableMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger().debug("PathParam annotation detected (" + paramObj.getName() + ")");
				}

				// Detect if is a query variable
				final MergedAnnotation requestParamMA = mergedAnnotations.get("jakarta.ws.rs.QueryParam");
				if(requestParamMA.isPresent()) {
					if(paramObj.isMultipartFile()) {
						paramObj.setLocation(ParameterLocation.BODY);
					} else {
						paramObj.setLocation(ParameterLocation.QUERY);
					}

					// The value is equivalent to the name (alias for and user of MergedAnnotation)
					final String value = requestParamMA.getString("value");
					if(!StringUtils.isEmpty(value)) {
						paramObj.setName(value);
					}
					context.getLogger().debug(
						"QueryParam annotation detected (" + paramObj.getName() + "), location is "
							+ paramObj.getLocation().toString());
				}

				// Detect if is a request body parameter (if it is not a path or a query param)
				if(paramObj.getLocation() == null) {
					if(bodyParameterDetected) {
						bodyParameterDetected = true;
						context.getLogger().error("Cannot set multiple body parameters, (" + paramObj.getName() + ")");
					} else {
						paramObj.setLocation(ParameterLocation.BODY);
						context.getLogger().debug(
							"Body parameter detected (" + paramObj.getName() + "), location is "
								+ paramObj.getLocation().toString());
					}
				}

				// Add eventual extra information given by a swagger annotation
				this.setSwaggerAnnotatedParameterProperties(parameter, mergedAnnotations, paramObj);
			}
		}

		return parameters.values().stream().filter(x -> x.getLocation() != null).collect(Collectors.toList());
	}

	@Override
	protected List<String> readEndpointPaths(final String basePath, final MergedAnnotation pathMergedAnnotation) {
		final String path = pathMergedAnnotation.getString("value");
		if(path == null) {
			return Arrays.asList(concatenateBasePathAndMethodPath(basePath, "", apiConfiguration.getPathEnhancement()));
		}
		return Arrays.asList(concatenateBasePathAndMethodPath(basePath, path, apiConfiguration.getPathEnhancement()));
	}

	@Override
	protected void setConsumeProduceProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException {
		final MergedAnnotation consumesMergedAnnotation = mergedAnnotations.get("jakarta.ws.rs.Consumes");
		final MergedAnnotation producesMergedAnnotation = mergedAnnotations.get("jakarta.ws.rs.Produces");

		final Optional<ParameterObject> body = endpoint.getParameters().stream()
			.filter(x -> ParameterLocation.BODY == x.getLocation())
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
		return 200;
	}

	@Override
	protected Type readResponseMethodType(final Method method, final MergedAnnotations mergedAnnotations) {
		if(responseAnnotation != null && mergedAnnotations.isPresent(responseAnnotation.getCanonicalName())) {
			return (Class) mergedAnnotations.get(responseAnnotation.getCanonicalName()).getValue("value").get();
		}
		return method.getGenericReturnType();
	}

	private enum JakartaRsHttpVerb {
		GET("jakarta.ws.rs.GET"),
		PUT("jakarta.ws.rs.PUT"),
		POST("jakarta.ws.rs.POST"),
		DELETE("jakarta.ws.rs.DELETE"),
		PATCH("jakarta.ws.rs.PATCH"),
		OPTIONS("jakarta.ws.rs.OPTIONS"),
		HEAD("jakarta.ws.rs.HEAD");

		private final String annotationClassName;

		JakartaRsHttpVerb(final String annotationClassName) {
			this.annotationClassName = annotationClassName;
		}

		public Class getAnnotationClass(final ClassLoaderHelper classLoaderHelper) {
			return classLoaderHelper.getByNameRuntimeEx(annotationClassName);
		}

		public String getAnnotationClassName() {
			return annotationClassName;
		}
	}

}
