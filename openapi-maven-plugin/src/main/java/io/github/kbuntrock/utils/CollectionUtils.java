package io.github.kbuntrock.utils;

import java.util.Collection;

/**
 * Small collection helpers used throughout the plugin.
 */
public final class CollectionUtils {

	/**
	 * Private Constructor
	 */
	private CollectionUtils() {
	}

	/**
	 * Null-safe check if the collection is empty.
	 *
	 * @return true if null or empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
}
