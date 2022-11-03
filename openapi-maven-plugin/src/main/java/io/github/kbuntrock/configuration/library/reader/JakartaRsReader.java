package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.OperationType;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.ParameterLocation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class JakartaRsReader extends AstractLibraryReader {

	public JakartaRsReader(final ApiConfiguration apiConfiguration) {
		super(apiConfiguration);
	}

	@Override
	public List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations) {
		List<String> basePaths = Collections.singletonList("");
		final MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
		if(requestMappingMergedAnnotation.isPresent()) {
			final String path = requestMappingMergedAnnotation.getString("value");
			if(!StringUtils.isEmpty(path)) {
				basePaths = Collections.singletonList(path);
			}
		}
		return basePaths;
	}

	@Override
	public void computeAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag)
		throws MojoFailureException {

		final MergedAnnotation<Path> requestMappingMergedAnnotation = mergedAnnotations.get(Path.class);
		if(requestMappingMergedAnnotation.isPresent()) {

			for(final JakartaRsHttpVerb verb : JakartaRsHttpVerb.values()) {
				final MergedAnnotation m = mergedAnnotations.get(verb.getAnnotationClass());
				if(m.isPresent()) {
					final String methodIdentifier = JavaClassAnalyser.createIdentifier(method);
					final List<ParameterObject> parameterObjects = readParameters(method);
					final DataObject responseObject = readResponseObject(method);
					final int responseCode = readResponseCode(null);
					final String path = readEndpointPaths(basePath, requestMappingMergedAnnotation).get(0);
					final Endpoint endpoint = new Endpoint();
					endpoint.setType(OperationType.fromJakarta(verb.getAnnotationClass()));
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
	protected List<ParameterObject> readParameters(final Method originalMethod) {
		logger.debug("Reading parameters from " + originalMethod.getName());

		// Set of the method in the original class and eventually the methods in the parent classes / interfaces
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);

		final Map<String, ParameterObject> parameters = new LinkedHashMap<>();

		for(final Method method : overridenMethods) {

			boolean bodyParameterDetected = false;

			for(final Parameter parameter : method.getParameters()) {
				if(HttpServletRequest.class.isAssignableFrom(parameter.getType()) ||
					jakarta.servlet.http.HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
					continue;
				}
				logger.debug("Parameter : " + parameter.getName());

				final ParameterObject paramObj = parameters.computeIfAbsent(parameter.getName(),
					(name) -> new ParameterObject(name, parameter.getParameterizedType()));

				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter,
					MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

				final MergedAnnotation<NotNull> notnullMA = mergedAnnotations.get(NotNull.class);
				// Detect if required
				if(notnullMA.isPresent()) {
					paramObj.setRequired(notnullMA.isPresent());
				} else {
					paramObj.setRequired(mergedAnnotations.get(jakarta.validation.constraints.NotNull.class).isPresent());
				}

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

	@Override
	protected int readResponseCode(final MergedAnnotations mergedAnnotations) {
		return HttpStatus.OK.value();
	}

	private enum JakartaRsHttpVerb {
		GET(jakarta.ws.rs.GET.class),
		PUT(jakarta.ws.rs.PUT.class),
		POST(jakarta.ws.rs.POST.class),
		DELETE(jakarta.ws.rs.DELETE.class),
		PATCH(jakarta.ws.rs.PATCH.class),
		OPTIONS(jakarta.ws.rs.OPTIONS.class),
		HEAD(jakarta.ws.rs.HEAD.class);

		private final Class annotationClass;

		JakartaRsHttpVerb(final Class annotationClass) {
			this.annotationClass = annotationClass;
		}

		public Class getAnnotationClass() {
			return annotationClass;
		}
	}


}
