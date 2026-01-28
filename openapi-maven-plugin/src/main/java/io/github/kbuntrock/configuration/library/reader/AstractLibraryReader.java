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
import java.util.*;

public abstract class AstractLibraryReader {

	protected final ApiContext context;

	protected final ApiConfiguration apiConfiguration;
	protected final GenericityResolver genericityResolver;

	protected final OpenApiTypeResolver openApiTypeResolver;

	public AstractLibraryReader(final ApiContext context, final ApiConfiguration apiConfiguration,
		final OpenApiTypeResolver openApiTypeResolver) {
		this.context = context;
		this.apiConfiguration = apiConfiguration;
		this.openApiTypeResolver = openApiTypeResolver;
		this.genericityResolver = new GenericityResolver(context);
	}

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

		final MergedAnnotation parametersAnnotation = mergedAnnotations
			.get("io.swagger.v3.oas.annotations.Parameters");
		if(parametersAnnotation.isPresent()) {
			MergedAnnotation[] parametersArray = parametersAnnotation.getAnnotationArray("value");
			addParameters(parameterObjects, parametersArray);
		}

		if(parameterObjects.size() > 0)
			endpoint.setParameters(parameterObjects);
	}

	protected void setSwaggerAnnotatedParameterProperties(final Parameter javaParameter,
		final MergedAnnotations mergedAnnotations, ParameterObject parameter) {
		MergedAnnotation parameterAnn = mergedAnnotations.get("io.swagger.v3.oas.annotations.Parameter");
		if(parameterAnn.isPresent()) {
			final String description = parameterAnn.getString("description");
			if(StringUtils.isNotBlank(description)) {
				parameter.setDescription(description);
			}
			final String name = parameterAnn.getString("name");
			if(StringUtils.isNotBlank(name)) {
				parameter.setName(name);
			}
			final String example = parameterAnn.getString("example");
			if(StringUtils.isNotBlank(example)) {
				parameter.setExample(example);
			}
			context.getLogger().debug("Found @Parameter " + name
				+ " param '" + parameter.getName() + "' : " + description);
		}
	}

	private void addParameters(ArrayList<ParameterObject> parameterObjects, MergedAnnotation[] parametersArray) {
		for(MergedAnnotation parameterAnnotation : parametersArray) {
			final String paramName = parameterAnnotation.getString("name");
			final String paramIn = parameterAnnotation.getValue("in").orElse(null).toString();
			final String paramDescription = parameterAnnotation.getString("description");
			final Boolean paramRequired = parameterAnnotation.getBoolean("required");
			MergedAnnotation schemaAnn = parameterAnnotation.getAnnotation("schema");
			final String paramType = (schemaAnn != null) ? schemaAnn.getString("type") : null;
			final String paramExample = parameterAnnotation.getString("example");

			ParameterObject paramObj = new ParameterObject(paramName, mapSchemaTypeToJavaType(paramType), openApiTypeResolver);
			paramObj.setLocation(ParameterLocation.fromValue("".equals(paramIn) ? "query" : paramIn));
			paramObj.setRequired(paramRequired);
			paramObj.setDescription(paramDescription);
			paramObj.setExample(paramExample);
			paramObj.setSchemaReferenceName(paramExample);
			parameterObjects.add(paramObj);
		}

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
