package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.model.annotation.OperationAnnotationInfo;
import io.github.kbuntrock.model.annotation.OperationResponse;
import io.github.kbuntrock.reflection.GenericityResolver;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.ParameterLocation;
import io.github.kbuntrock.utils.UnwrappingType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base reader that translates framework/library-specific annotations (Spring Web, JAX‑RS, Jakarta) into the plugin's common
 * OpenAPI-oriented model.
 * <p>
 * Responsibilities:
 * - Compute base and method paths
 * - Extract HTTP method, parameters, request/response bodies
 * - Interpret Swagger/OpenAPI annotations when present
 * - Unwrap framework container types where needed (responses/parameters)
 * </p>
 */
public abstract class AbstractLibraryReader {

	protected final ApiContext context;

	protected final ApiConfiguration apiConfiguration;
	protected final GenericityResolver genericityResolver;

	protected final OpenApiTypeResolver openApiTypeResolver;

	public AbstractLibraryReader(final ApiContext context, final ApiConfiguration apiConfiguration,
		final OpenApiTypeResolver openApiTypeResolver) {
		this.context = context;
		this.apiConfiguration = apiConfiguration;
		this.openApiTypeResolver = openApiTypeResolver;
		this.genericityResolver = new GenericityResolver(context);
	}

	/**
	 * Concatenate base path and method path with optional automatic separators and leading slash.
	 *
	 * @param basePath
	 *            controller-level base path
	 * @param methodPath
	 *            method-level path
	 * @param automaticSeparator
	 *            whether to auto-insert separators and a leading slash
	 * @return normalized path
	 */
	protected static String concatenateBasePathAndMethodPath(final String basePath, final String methodPath,
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
	 * Resolve the response {@link DataObject} for a method, honoring library-specific return wrappers and excluding non-documentable responses.
	 *
	 * @param clazz
	 *            declaring class
	 * @param method
	 *            endpoint method
	 * @param mergedAnnotations
	 *            merged annotations view
	 * @return response {@link DataObject} or {@code null} if not documentable
	 */
	protected DataObject readResponseObject(final Class<?> clazz, final Method method,
		final MergedAnnotations mergedAnnotations) {
		final Class<?> returnType = method.getReturnType();
		if(Void.class == returnType || Void.TYPE == returnType || !openApiTypeResolver.canResponseBeDocumented(returnType)) {
			return null;
		}

		DataObject dataObject = new DataObject(
			genericityResolver.resolve(clazz, readResponseMethodType(method, mergedAnnotations)), openApiTypeResolver);
		dataObject = computeFrameworkReturnObject(dataObject);
		context.getLogger().debug(dataObject.toString());
		return dataObject;
	}

	/**
	 * Read the generic method return type, possibly overridden by library-specific behavior.
	 */
	protected Type readResponseMethodType(final Method method, final MergedAnnotations mergedAnnotations) {
		return method.getGenericReturnType();
	}

	/**
	 * Some returned objects are handled in a specific manner by spring.
	 * In that case, we have to adapt it
	 *
	 * @param dataObject
	 *            source
	 * @return return DataObject
	 */
	private DataObject computeFrameworkReturnObject(final DataObject dataObject) {
		return openApiTypeResolver.unwrapDataObject(dataObject, UnwrappingType.RESPONSE);
	}

	/**
	 * Determine if a method is effectively deprecated, considering overrides in the hierarchy.
	 */
	protected boolean isDeprecated(final Method originalMethod) {
		final Set<Method> overridenMethods = MethodUtils.getOverrideHierarchy(originalMethod, ClassUtils.Interfaces.INCLUDE);
		for(final Method method : overridenMethods) {
			if(method.getDeclaredAnnotation(Deprecated.class) != null) {
				return true;
			}
		}
		return false;
	}

	public abstract List<String> readBasePaths(final Class<?> clazz, final MergedAnnotations mergedAnnotations);

	public abstract void computeAnnotations(final Class<?> clazz, final String basePath, final Method method,
		final MergedAnnotations mergedAnnotations,
		final Tag tagr) throws MojoFailureException;

	protected abstract List<ParameterObject> readParameters(final Class<?> clazz, final Method originalMethod,
		final MergedAnnotations endpointAnnotations);

	protected abstract List<String> readEndpointPaths(String basePath,
		MergedAnnotation requestMappingMergedAnnotation);

	protected abstract void setConsumeProduceProperties(Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException;

	protected abstract int readResponseCode(MergedAnnotations mergedAnnotations);

	protected ParameterObject unwrapParameterObject(final ParameterObject parameterObject) {
		final DataObject dataObject = openApiTypeResolver.unwrapDataObject(parameterObject, UnwrappingType.PARAMETER);
		// Pointer equality is intentional
		if(parameterObject == dataObject) {
			return parameterObject;
		}
		return new ParameterObject(parameterObject.getName(), dataObject);
	}

	protected void setSwaggerAnnotatedEndpointProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations) {
		ArrayList<ParameterObject> parameterObjects = new ArrayList<ParameterObject>();

		final MergedAnnotation operationAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.Operation");
		if(operationAnnotation.isPresent()) {
			OperationAnnotationInfo operationInfo = endpoint.getOperationAnnotationInfo();
			final String operationId = operationAnnotation.getString("operationId");
			if(!StringUtils.isEmpty(operationId)) {
				operationInfo.setOperationId(operationId);
			}

			final String summary = operationAnnotation.getString("summary");
			if(!StringUtils.isEmpty(summary)) {
				operationInfo.setSummary(summary);
			}

			final String description = operationAnnotation.getString("description");
			if(!StringUtils.isEmpty(description)) {
				operationInfo.setDescription(description);
			}

			MergedAnnotation[] parametersArray = operationAnnotation.getAnnotationArray("parameters");
			addParameters(parameterObjects, parametersArray);

			MergedAnnotation[] responseArray = operationAnnotation.getAnnotationArray("responses");

			for(MergedAnnotation responseAnnotation : responseArray) {
				final OperationResponse operationResponse = new OperationResponse();
				final String responseCode = responseAnnotation.getString("responseCode");

				if("default".equals(responseCode)) {
					operationResponse.setCode(200);
				} else {
					try {
						operationResponse.setCode(Integer.parseInt(responseCode));
					} catch(NumberFormatException e) {
						context.getLogger().warn("Invalid response code '" + responseCode + "' for operation "
							+ operationInfo.getOperationId() + ". Skipping response.");
						continue;
					}
				}

				final String responseDescription = responseAnnotation.getString("description");
				if(!StringUtils.isEmpty(responseDescription)) {
					operationResponse.setDescription(responseDescription);
				}

				final MergedAnnotation[] contentArray = responseAnnotation.getAnnotationArray("content");
				if(contentArray.length > 1) {
					context.getLogger().warn("Multiple content annotations found for response code " + responseCode
						+ " and operation " + operationInfo.getOperationId() + ". Only the first one will be used.");
				}
				Optional<MergedAnnotation> optionalContent = Arrays.stream(contentArray).findFirst();
				if(optionalContent.isPresent()) {
					final MergedAnnotation content = optionalContent.get();
					final MergedAnnotation schema = content.getAnnotation("schema");
					if(schema.isPresent()) {
						final Class<?> implementation = schema.getClass("implementation");
						if(implementation != null && !Void.class.equals(implementation) && !Void.TYPE.equals(implementation)) {
							final DataObject responseObject = new DataObject(implementation, openApiTypeResolver);
							operationResponse.setDataObject(responseObject);
						}
					}
				}
				operationInfo.addResponse(operationResponse);
			}
		}

		final MergedAnnotation parametersAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.Parameters");
		if(parametersAnnotation.isPresent()) {
			MergedAnnotation[] parametersArray = parametersAnnotation.getAnnotationArray("value");
			addParameters(parameterObjects, parametersArray);
		} else {
			final MergedAnnotation parameterAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.Parameter");
			if(parameterAnnotation.isPresent()) {
				parameterObjects.add(buildParameter(parameterAnnotation));
			}
		}

		if(!parameterObjects.isEmpty()) {
			List<ParameterObject> mergeParameterList = mergeParameterLists(parameterObjects, endpoint.getParameters());
			endpoint.setParameters(mergeParameterList);
		}
	}

	protected List<ParameterObject> mergeParameterLists(List<ParameterObject> parameterObjectListAtOperationLevel,
		List<ParameterObject> parameterObjectListIntoMethod) {
		List<ParameterObject> l1 = (parameterObjectListAtOperationLevel == null)
			? Collections.emptyList()
			: parameterObjectListAtOperationLevel;
		List<ParameterObject> l2 = (parameterObjectListIntoMethod == null)
			? Collections.emptyList()
			: parameterObjectListIntoMethod;

		return new ArrayList<>(Stream.concat(l1.stream(), l2.stream())
			.collect(Collectors.toMap(
				ParameterObject::getName,
				p -> p,
				this::mergeParameter,
				LinkedHashMap::new))
			.values());
	}

	private ParameterObject mergeParameter(ParameterObject replacement, ParameterObject privileged) {
		if(replacement == null)
			return privileged;

		if(StringUtils.isEmpty(privileged.getDescription())) {
			privileged.setDescription(replacement.getDescription());
		}

		if(privileged.getLocation() == null) {
			privileged.setLocation(replacement.getLocation());
		}

		if(StringUtils.isEmpty(privileged.getExample())) {
			privileged.setExample(replacement.getExample());
		}

		if(StringUtils.isEmpty(privileged.getSchemaReferenceName())) {
			privileged.setSchemaReferenceName(replacement.getSchemaReferenceName());
		}

		privileged.setRequired(replacement.isRequired() || privileged.isRequired());

		return privileged;
	}

	protected void setSwaggerAnnotatedParameterProperties(final Parameter javaParameter,
		final MergedAnnotations mergedAnnotations, ParameterObject parameter) {
		MergedAnnotation parameterAnn = mergedAnnotations.get("io.swagger.v3.oas.annotations.Parameter");
		if(parameterAnn.isPresent()) {
			setParameter(parameter, parameterAnn);
		}
	}

	private void setParameter(ParameterObject parameter, MergedAnnotation parameterAnn) {
		final ParameterObject data = buildParameter(parameterAnn);
		if(StringUtils.isNotBlank(data.getName())) {
			parameter.setName(data.getName());
		}

		parameter.setDescription(data.getDescription());
		parameter.setExample(data.getExample());
		parameter.setSchemaReferenceName(data.getSchemaReferenceName());
		// parameter.setLocation(data.getLocation());
		// parameter.setRequired(data.isRequired());

		context.getLogger().debug("Found @Parameter " + data.getName()
			+ " param '" + parameter.getName() + "' : " + parameter.getDescription());
	}

	private void addParameters(ArrayList<ParameterObject> parameterObjects, MergedAnnotation[] parametersArray) {
		for(MergedAnnotation parameterAnnotation : parametersArray) {
			parameterObjects.add(buildParameter(parameterAnnotation));
		}
	}

	private ParameterObject buildParameter(MergedAnnotation parameterAnnotation) {
		final String name = parameterAnnotation.getString("name");

		MergedAnnotation schema = parameterAnnotation.getAnnotation("schema").isPresent()
			? parameterAnnotation.getAnnotation("schema")
			: null;
		final String paramType = (schema != null) ? schema.getString("type") : null;

		ParameterObject parameter = new ParameterObject(name, mapSchemaTypeToJavaType(paramType), openApiTypeResolver);

		Boolean required = Optional.of(parameterAnnotation.getBoolean("required"))
			.map(Optional::of)
			.orElseGet(() -> Optional.ofNullable(schema).map(s -> s.getBoolean("required")))
			.orElse(null);
		parameter.setRequired(Boolean.TRUE.equals(required));

		// Priority: @Parameter(description = ...) > @Schema(description = ...) per OpenAPI 3 Specs
		final String description = Optional.ofNullable(
			Optional.ofNullable(parameterAnnotation.getString("description"))
				.filter(StringUtils::isNotEmpty)
				.orElseGet(() -> schema != null ? schema.getString("description") : null))
			.filter(StringUtils::isNotEmpty)
			.orElse(null);
		parameter.setDescription(description);

		// Priority: @Parameter(example = ...) > @Schema(example = ...) per OpenAPI 3 Specs
		final String example = Optional.ofNullable(
			Optional.ofNullable(parameterAnnotation.getString("example"))
				.filter(StringUtils::isNotEmpty)
				.orElseGet(() -> schema != null ? schema.getString("example") : null))
			.filter(StringUtils::isNotEmpty)
			.orElse(null);

		parameter.setExample(example);
		parameter.setSchemaReferenceName(example);

		final String paramIn = parameterAnnotation.getValue("in").orElse(null).toString();
		parameter.setLocation(ParameterLocation.fromValue("".equals(paramIn) ? "query" : paramIn));

		return parameter;
	}

	private static Class<?> mapSchemaTypeToJavaType(String schemaType) {
		if(schemaType == null)
			return Object.class;

		switch(schemaType.trim().toLowerCase()) {
			case "string":
				return String.class;
			case "integer":
				return Integer.class;
			case "number":
				return Double.class;
			case "boolean":
				return Boolean.class;
			case "array":
				return java.util.List.class;
			case "object":
				return java.util.Map.class;
			default:
				return Object.class;
		}
	}

}
