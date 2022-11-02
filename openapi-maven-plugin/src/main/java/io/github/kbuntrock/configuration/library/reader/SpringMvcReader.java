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
import java.lang.annotation.Annotation;
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
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	public void computeAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations, final Tag tag)
		throws MojoFailureException {

		final MergedAnnotation<RequestMapping> requestMappingMergedAnnotation = mergedAnnotations.get(RequestMapping.class);
		if(requestMappingMergedAnnotation.isPresent()) {
			final RequestMethod[] requestMethods = requestMappingMergedAnnotation.getEnumArray("method", RequestMethod.class);
			if(requestMethods.length > 0) {
				logger.debug("Parsing request method : " + method.getName());
				final String methodIdentifier = JavaClassAnalyser.createIdentifier(method);
				final List<ParameterObject> parameterObjects = readParameters(method);
				final DataObject responseObject = readResponseObject(method);
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

	@Override
	protected List<ParameterObject> readParameters(final Method originalMethod) {
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

	@Override
	protected List<String> readEndpointPaths(final String basePath,
		final MergedAnnotation<? extends Annotation> requestMappingMergedAnnotation) {

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
