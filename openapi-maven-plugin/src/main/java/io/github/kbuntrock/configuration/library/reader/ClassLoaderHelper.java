package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.MojoRuntimeException;

import java.util.HashMap;
import java.util.Map;

public final class ClassLoaderHelper {

    private final ClassLoader classLoader;
	private final Map<String, Class> map = new HashMap<>();

    public ClassLoaderHelper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class getByName(final String canonicalName) throws ClassNotFoundException {

		Class clazz = map.get(canonicalName);
		if(clazz == null) {
			map.put(canonicalName, Class.forName(canonicalName, true, classLoader));
			clazz = map.get(canonicalName);
		}
		return clazz;

	}

	public Class getByNameRuntimeEx(final String canonicalName) {

		try {
			return getByName(canonicalName);
		} catch(final ClassNotFoundException e) {
			throw new MojoRuntimeException(
				canonicalName + " cannot be loaded. Please check if the correct dependencies are in your project classpath.", e);
		}

	}

	public boolean isClass(final String canonicalName) {
		try {
			Class<?> clazz = getByName(canonicalName);
			return clazz != null;
		} catch(ClassNotFoundException e) {
			return false;
		}
	}
}
