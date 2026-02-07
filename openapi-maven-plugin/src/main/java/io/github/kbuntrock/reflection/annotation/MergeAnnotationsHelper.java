package io.github.kbuntrock.reflection.annotation;

import java.lang.reflect.AnnotatedElement;

/**
 * Factory abstraction for obtaining a {@link MergedAnnotations} view from a given
 * {@link AnnotatedElement}.
 * <p>
 * Implementations may delegate to Spring's annotation merging facilities or use a
 * lightweight in-house strategy; the returned facade exposes a consistent API to
 * query merged annotation attributes regardless of the underlying mechanism.
 * </p>
 *
 * @see io.github.kbuntrock.reflection.annotation.spring.SpringMergeAnnotationsHelper
 * @see io.github.kbuntrock.reflection.annotation.regular.RegularMergedAnnotationsHelper
 */
public interface MergeAnnotationsHelper {

	/**
	 * Create a merged-annotations view for the supplied element.
	 *
	 * @param element
	 *            the annotated element (e.g. {@link Class}, {@link java.lang.reflect.Method},
	 *            {@link java.lang.reflect.Field}, or {@link java.lang.reflect.Parameter}); must not be {@code null}
	 * @return a non-null facade to query merged annotations on the element
	 */
	MergedAnnotations from(AnnotatedElement element);
}
