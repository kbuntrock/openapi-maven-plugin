package io.github.kbuntrock.reflection.annotation;

import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;

/**
 * Spring annotation resolution is a mechanism that is very specific to this ecosystem. Even though this plugin does not use all of its
 * capabilities, reimplementing the algorithm, even in a limited way, is far from trivial — especially regarding the handling of
 * meta-annotations (annotations on annotations) and aliases.
 *
 * However, for this plugin to work with Spring, it already requires the spring-web library, which itself has transitive dependencies on
 * spring-beans and spring-core, where the annotation resolution mechanism is implemented.
 * The simplest and most efficient approach is therefore to call Spring’s functions via reflection.
 */
public class MergeAnnotationsHelper {

	public static String ERROR_MSG_SUFFIX = " Please open an issue on github and indicate the Spring version used in your project.";

	// MergeAnnotations block
	Class<?> springMergedAnnotations;
	private final Method staticFromMethod;
	Method maMethodGet;
	Method maMethodIsPresent;
	Enum<?> springSearchStrategy;

	// MergeAnnotation block
	Class<?> springMergedAnnotation;
	Method methodIsPresent;
	Method methodGetString;
	Method methodGetStringArray;
	Method methodGetClass;
	Method methodGetBoolean;
	Method methodGetInt;
	Method methodGetEnumArray;
	Method methodGetAnnotationArray;
	Method methodGetAnnotation;
	Method methodGetValue;

	public MergeAnnotationsHelper(ClassLoaderHelper classLoaderHelper) {
		springMergedAnnotations = classLoaderHelper.getByNameRuntimeEx("org.springframework.core.annotation.MergedAnnotations");
		springMergedAnnotation = classLoaderHelper.getByNameRuntimeEx("org.springframework.core.annotation.MergedAnnotation");
		Class<?> searchStrategyClass = classLoaderHelper
			.getByNameRuntimeEx("org.springframework.core.annotation.MergedAnnotations$SearchStrategy");

		try {
			staticFromMethod = springMergedAnnotations.getMethod("from", AnnotatedElement.class, searchStrategyClass);
			if(!Modifier.isStatic(staticFromMethod.getModifiers())) {
				throw new IllegalStateException(
					"Spring MergedAnnotations \"from\" method is expected to be static." + ERROR_MSG_SUFFIX);
			}
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotations \"from\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			springSearchStrategy = Enum.valueOf(searchStrategyClass.asSubclass(Enum.class), "TYPE_HIERARCHY");
		} catch(IllegalArgumentException e) {
			throw new MojoRuntimeException(
				"Cannot load TYPE_HIERARCHY enum from org.springframework.core.annotation.SearchStrategy." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			maMethodGet = springMergedAnnotations.getMethod("get", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotations \"get\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			maMethodIsPresent = springMergedAnnotations.getMethod("isPresent", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotations \"isPresent\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodIsPresent = springMergedAnnotation.getMethod("isPresent");
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"isPresent\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetString = springMergedAnnotation.getMethod("getString", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getString\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetStringArray = springMergedAnnotation.getMethod("getStringArray", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getStringArray\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetClass = springMergedAnnotation.getMethod("getClass", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getClass\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetBoolean = springMergedAnnotation.getMethod("getBoolean", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getBoolean\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetInt = springMergedAnnotation.getMethod("getInt", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getInt\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetEnumArray = springMergedAnnotation.getMethod("getEnumArray", String.class, Class.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getEnumArray\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetAnnotationArray = springMergedAnnotation.getMethod("getAnnotationArray", String.class, Class.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getAnnotationArray\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetAnnotation = springMergedAnnotation.getMethod("getAnnotation", String.class, Class.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getAnnotation\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
		try {
			methodGetValue = springMergedAnnotation.getMethod("getValue", String.class);
		} catch(NoSuchMethodException e) {
			throw new MojoRuntimeException(
				"Spring MergedAnnotation \"getValue\" method load error." + ERROR_MSG_SUFFIX,
				e);
		}
	}

	public MergedAnnotations from(AnnotatedElement element) {
		try {
			Object result = staticFromMethod.invoke(null, element, springSearchStrategy);
			return new MergedAnnotations(result, this);
		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new MojoRuntimeException("Cannot invoke spring MergeAnnotations from method." + ERROR_MSG_SUFFIX, e);
		}
	}

	public <A> A invokeWrapping(String methodName, Callable<A> callable) {
		try {
			return callable.call();
		} catch(Exception e) {
			throw new MojoRuntimeException("Cannot invoke spring " + methodName + "." + ERROR_MSG_SUFFIX, e);
		}
	}
}
