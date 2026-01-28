package io.github.kbuntrock.reflection.annotation;

import java.util.Optional;

/**
 * Represents a merged view of a target annotation discovered on an annotated element.
 * <p>
 * This interface is a subset of the corresponding interface located in spring-core.
 * It is implemented in two variants:
 * - Spring: direct call to the Spring implementation (with alias handling)
 * - Regular: a lighter in-house implementation (no alias handling)
 * <p>
 * This interface provides typed accessors for common attribute shapes and for nested
 * annotations. Unless otherwise noted, callers should first verify {@link #isPresent()}
 * before accessing attributes; attempting to read attributes of a non-present merged
 * annotation is expected to result in an exception from the implementation.
 * </p>
 */
public interface MergedAnnotation {

	/**
	 * Whether the target annotation is present in the merged view.
	 * <p>
	 * Presence can be satisfied by a direct declaration, by a meta-annotation,
	 * or by a declaration discovered via the element's hierarchy.
	 * </p>
	 *
	 * @return {@code true} if present, {@code false} otherwise
	 */
	boolean isPresent();

	/**
	 * Return the value of the named attribute as a {@link String}.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value as a string
	 */
	String getString(String attributeName);

	/**
	 * Return the value of the named attribute as a {@code String[]} array.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value as an array of strings
	 */
	String[] getStringArray(String attributeName);

	/**
	 * Return the value of the named attribute as a {@link Class} reference.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value as a class
	 */
	Class<?> getClass(String attributeName);

	/**
	 * Return the value of the named attribute as a {@code boolean}.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value as a boolean
	 */
	boolean getBoolean(String attributeName);

	/**
	 * Return the value of the named attribute as an {@code int}.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value as an int
	 */
	int getInt(String attributeName);

	/**
	 * Return the value of the named attribute as an array of enum names.
	 * <p>
	 * The underlying attribute is expected to be an enum array; this method returns
	 * the {@link Enum#name()} of each constant.
	 * </p>
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the enum constant names
	 */
	String[] getEnumArrayAsString(String attributeName);

	/**
	 * Return the value of the named attribute as an array of nested annotations, each wrapped as a merged view.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return merged views of the nested annotations
	 */
	MergedAnnotation[] getAnnotationArray(String attributeName);

	/**
	 * Return the value of the named attribute as a nested annotation wrapped as a merged view.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return a merged view of the nested annotation
	 */
	MergedAnnotation getAnnotation(String attributeName);

	/**
	 * Return the raw value of the named attribute, if present.
	 * <p>
	 * Unlike the typed getters, this method exposes the underlying merged value
	 * without conversion.
	 * </p>
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return an {@link Optional} containing the value if present, or empty if absent
	 */
	Optional<Object> getValue(String attributeName);
}
