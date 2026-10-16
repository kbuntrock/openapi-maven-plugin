package io.github.kbuntrock.reflection;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/** Discovers concrete subclasses and implementations from an open ClassGraph scan. */
public final class SubtypeResolver {

	private SubtypeResolver() {
	}

	/**
	 * ClassGraph resolves subclasses and implementations transitively.
	 *
	 * @param baseClass
	 *            class whose subtypes must be searched
	 * @param scanResult
	 *            open scan result; may be {@code null}
	 * @return concrete subtypes of {@code baseClass}
	 */
	public static List<Class<?>> findKnownSubtypes(final Class<?> baseClass, final ScanResult scanResult) {
		if(scanResult == null) {
			return new ArrayList<>();
		}
		final ClassInfo classInfo = scanResult.getClassInfo(baseClass.getName());
		if(classInfo == null) {
			return new ArrayList<>();
		}
		final List<ClassInfo> subtypeClassInfos = baseClass.isInterface()
			? classInfo.getClassesImplementing()
			: classInfo.getSubclasses();

		return subtypeClassInfos.stream()
			.filter(subtypeClassInfo -> !subtypeClassInfo.isAbstract() && !subtypeClassInfo.isInterface())
			.map(ClassInfo::loadClass)
			.sorted(Comparator.comparingInt((Class<?> subtype) -> inheritanceDistance(baseClass, subtype))
				.thenComparing(Class::getName))
			.collect(Collectors.toList());
	}

	private static int inheritanceDistance(final Class<?> baseClass, final Class<?> subtype) {
		final Queue<Class<?>> pendingClasses = new ArrayDeque<>();
		final Map<Class<?>, Integer> distances = new HashMap<>();
		pendingClasses.add(subtype);
		distances.put(subtype, 0);

		while(!pendingClasses.isEmpty()) {
			final Class<?> currentClass = pendingClasses.remove();
			final int currentDistance = distances.get(currentClass);
			if(baseClass.equals(currentClass)) {
				return currentDistance;
			}
			addParent(currentClass.getSuperclass(), currentDistance, pendingClasses, distances);
			for(final Class<?> currentInterface : currentClass.getInterfaces()) {
				addParent(currentInterface, currentDistance, pendingClasses, distances);
			}
		}
		return Integer.MAX_VALUE;
	}

	private static void addParent(final Class<?> parent,
		final int currentDistance,
		final Queue<Class<?>> pendingClasses,
		final Map<Class<?>, Integer> distances) {
		if(parent != null && !distances.containsKey(parent)) {
			distances.put(parent, currentDistance + 1);
			pendingClasses.add(parent);
		}
	}
}
