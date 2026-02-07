package io.github.kbuntrock.reflection.annotation;

/**
 * Access point for obtaining merged views of annotations present on a given
 * annotated element.
 * <p>
 * This interface is a subset of the corresponding interface located in spring-core.
 * It is implemented in two variants:
 * - Spring: direct call to the Spring implementation (with alias handling)
 * - Regular: a lighter in-house implementation (no alias handling)
 * </p>
 */
public interface MergedAnnotations {

	/**
	 * Return a merged view of the annotation with the given fully qualified type name.
	 * <p>
	 * If the specified annotation type is not on the classpath, is not an annotation, or is not present in the merged view of the underlying
	 * element, an implementation should return a {@link MergedAnnotation} whose {@link MergedAnnotation#isPresent()} is {@code false}.
	 * </p>
	 *
	 * @param annotationType
	 *            the fully qualified name of the annotation type
	 * @return a merged view of the requested annotation (possibly not present)
	 */
	MergedAnnotation get(String annotationType);
}
