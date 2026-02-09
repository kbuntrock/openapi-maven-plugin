package io.github.kbuntrock.utils;

/**
 * Null-safety utilities complementing {@code java.util.Objects} for older JDKs.
 */
public class ObjectsUtils {

	/**
	 * Returns the first argument if it is non-{@code null} and
	 * otherwise returns the second argument.
	 *
	 * Derived from the Objects.requireNonNullElse defined only from jdk9 version
	 *
	 * @param <T>        type of the objects
	 * @param obj        primary value to test
	 * @param defaultObj fallback value when {@code obj} is {@code null}
	 * @return {@code obj} if non-null, otherwise {@code defaultObj}
	 */
	public static <T> T nonNullElse(T obj, T defaultObj) {
		return (obj != null) ? obj : defaultObj;
	}

}
