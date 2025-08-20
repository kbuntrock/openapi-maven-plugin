package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.model.annotation.OperationAnnotationInfo;
import io.github.kbuntrock.model.annotation.OperationResponse;
import io.github.kbuntrock.reflection.GenericityResolver;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.UnwrappingType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

public abstract class AstractLibraryReader {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected final ApiConfiguration apiConfiguration;
	protected final GenericityResolver genericityResolver = new GenericityResolver();

	public AstractLibraryReader(final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
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

	protected DataObject readResponseObject(final Class clazz, final Method method,
		final MergedAnnotations mergedAnnotations) {
		final Class<?> returnType = method.getReturnType();
		if(Void.class == returnType || Void.TYPE == returnType) {
			return null;
		}

		DataObject dataObject = new DataObject(
			genericityResolver.resolve(clazz, readResponseMethodType(method, mergedAnnotations)));
		dataObject = computeFrameworkReturnObject(dataObject);
		logger.debug(dataObject.toString());
		return dataObject;
	}

	protected Type readResponseMethodType(final Method method, final MergedAnnotations mergedAnnotations) {
		return method.getGenericReturnType();
	}

	/**
	 * Some returned objects are handled in a specific manner by spring.
	 * In that case, we have to adapt it
	 *
	 * @param dataObject source
	 * @return return DataObject
	 */
	private DataObject computeFrameworkReturnObject(final DataObject dataObject) {
		return OpenApiTypeResolver.INSTANCE.unwrapDataObject(dataObject, UnwrappingType.RESPONSE);
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

	public abstract void computeAnnotations(final Class clazz, final String basePath, final Method method, final MergedAnnotations mergedAnnotations,
		final Tag tagr) throws MojoFailureException;

	protected abstract List<ParameterObject> readParameters(final Class clazz, final Method originalMethod, final MergedAnnotations endpointAnnotations);

	protected abstract List<String> readEndpointPaths(String basePath,
		MergedAnnotation<? extends Annotation> requestMappingMergedAnnotation);

	protected abstract void setConsumeProduceProperties(Endpoint endpoint, final MergedAnnotations mergedAnnotations)
		throws MojoFailureException;

	protected abstract int readResponseCode(MergedAnnotations mergedAnnotations);

	protected ParameterObject unwrapParameterObject(final ParameterObject parameterObject) {
		final DataObject dataObject = OpenApiTypeResolver.INSTANCE.unwrapDataObject(parameterObject, UnwrappingType.PARAMETER);
		// Pointer equality is intentional
		if(parameterObject == dataObject) {
			return parameterObject;
		}
		return new ParameterObject(parameterObject.getName(), dataObject);
	}

	protected void setSwaggerAnnotatedEndpointProperties(final Endpoint endpoint, final MergedAnnotations mergedAnnotations){
		final MergedAnnotation<Annotation> operationAnnotation = mergedAnnotations.get("io.swagger.v3.oas.annotations.Operation");
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

			MergedAnnotation<Annotation>[] responseArray = operationAnnotation.getAnnotationArray("responses", Annotation.class);

			for (MergedAnnotation<Annotation> responseAnnotation : responseArray) {
				final OperationResponse operationResponse = new OperationResponse();
				final String responseCode = responseAnnotation.getString("responseCode");

				if ("default".equals(responseCode)) {
					operationResponse.setCode(200);
				} else {
					try {
						operationResponse.setCode(Integer.parseInt(responseCode));
					} catch (NumberFormatException e) {
						logger.warn("Invalid response code '" + responseCode + "' for operation " + operationInfo.getOperationId() + ". Skipping response.");
						continue;
					}
				}


				final String responseDescription = responseAnnotation.getString("description");
				if (!StringUtils.isEmpty(responseDescription)) {
					operationResponse.setDescription(responseDescription);
				}

				final MergedAnnotation<Annotation>[] contentArray = responseAnnotation.getAnnotationArray("content", Annotation.class);
				if (contentArray.length > 1) {
					logger.warn("Multiple content annotations found for response code " + responseCode + " and operation " + operationInfo.getOperationId() + ". Only the first one will be used.");
				}
				Optional<MergedAnnotation<Annotation>> optionalContent = Arrays.stream(contentArray).findFirst();
				if (optionalContent.isPresent()) {
					final MergedAnnotation<Annotation> content = optionalContent.get();
					final MergedAnnotation<Annotation> schema = content.getAnnotation("schema", Annotation.class);
					if (schema.isPresent()) {
						final Class<?> implementation = schema.getClass("implementation");
						if (implementation != null && !Void.class.equals(implementation) && !Void.TYPE.equals(implementation)) {
							final DataObject responseObject = new DataObject(implementation);
							operationResponse.setDataObject(responseObject);
						}
					}
				}
				operationInfo.addResponse(operationResponse);
			}
		}
	}
}
