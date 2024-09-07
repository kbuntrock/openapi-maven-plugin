package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassLoaderUtils {

	private static final Map<String, Class> map = new ConcurrentHashMap<>();

	public static Class getByName(final String canonicalName) throws ClassNotFoundException {

		Class clazz = map.get(canonicalName);
		if(clazz == null) {
			map.put(canonicalName, Class.forName(canonicalName, true, ReflectionsUtils.getProjectClassLoader()));
			clazz = map.get(canonicalName);
		}
		return clazz;

	}

	public static Class getByNameRuntimeEx(final String canonicalName) {

		try {
			return getByName(canonicalName);
		} catch(final ClassNotFoundException e) {
			throw new MojoRuntimeException(
				canonicalName + " cannot be loaded. Please check if the correct dependencies are in your project classpath.", e);
		}

	}

	public static boolean isClass(String canonicalName) {
		try {
			Class<?> clazz = getByName(canonicalName);
			return clazz != null;
		} catch(ClassNotFoundException e) {
			return false;
		}
	}
}
