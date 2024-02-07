package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ClassGenericityResolver;
import io.github.kbuntrock.utils.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpEntity;

public abstract class AstractLibraryReader {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected final ApiConfiguration apiConfiguration;

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

	protected DataObject readResponseObject(final Method method, final ClassGenericityResolver genericityResolver,
		final MergedAnnotations mergedAnnotations) {
		final Class<?> returnType = method.getReturnType();
		if(Void.class == returnType || Void.TYPE == returnType) {
			return null;
		}

		DataObject dataObject = new DataObject(
			genericityResolver.getContextualType(readResponseMethodType(method, mergedAnnotations), method));
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
		if(Optional.class.isAssignableFrom(dataObject.getJavaClass()) ||
			HttpEntity.class.isAssignableFrom(dataObject.getJavaClass())) {
			return new DataObject(dataObject.getGenericNameToTypeMap().get("T"));
		}
		return dataObject;
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

	public abstract List<Endpoint> readAnnotations(final String basePath, final Method method, final MergedAnnotations mergedAnnotations,
										 final ClassGenericityResolver genericityResolver) throws MojoFailureException;

	protected abstract List<ParameterObject> readParameters(Method originalMethod, final ClassGenericityResolver genericityResolver);

	protected abstract List<String> readEndpointPaths(String basePath,
		MergedAnnotation<? extends Annotation> requestMappingMergedAnnotation);

	protected abstract List<String> readConsumeProperties(Endpoint endpoint, final MergedAnnotations mergedAnnotations)
			throws MojoFailureException;

	protected abstract List<String> readProduceProperties(Endpoint endpoint, final MergedAnnotations mergedAnnotations)
			throws MojoFailureException;

	protected abstract int readResponseCode(MergedAnnotations mergedAnnotations);

}
