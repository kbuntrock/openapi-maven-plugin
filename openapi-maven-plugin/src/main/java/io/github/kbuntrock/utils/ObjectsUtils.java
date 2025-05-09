package io.github.kbuntrock.utils;

/**
 *
 */
public class ObjectsUtils {

	/**
	 * Returns the first argument if it is non-{@code null} and
	 * otherwise returns the second argument.
	 *
	 * Derived from the Objects.requireNonNullElse defined only from jdk9 version
	 */
	public static <T> T nonNullElse(T obj, T defaultObj) {
		return (obj != null) ? obj : defaultObj;
	}

}
