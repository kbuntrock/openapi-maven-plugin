package io.github.kbuntrock.configuration.library.reader;

import io.github.kbuntrock.MojoRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ClassLoaderHelper {

	private final ClassLoader classLoader;
	private final Map<String, Class> map = new HashMap<>();
	private final Map<String, Optional<Class>> tryMap = new HashMap<>();

	public ClassLoaderHelper(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Optional<Class> tryToGetByName(final String canonicalName) {
		if(tryMap.containsKey(canonicalName)) {
			return tryMap.get(canonicalName);
		}
		try {
			Optional<Class> clazz = Optional.of(Class.forName(canonicalName, true, classLoader));
			tryMap.put(canonicalName, clazz);
			return clazz;
		} catch(ClassNotFoundException e) {
			// This class is not in the classpath
			Optional<Class> empty = Optional.empty();
			tryMap.put(canonicalName, empty);
			return empty;
		}
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
