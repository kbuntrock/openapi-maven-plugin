package io.github.kbuntrock.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Small reflection helpers for walking fields/methods and adjusting accessibility in a safe, centralized way.
 */
public final class ReflectionsUtils {

	private ReflectionsUtils() {
		// Nothing to do
	}

	public static List<Field> getAllNonStaticFields(List<Field> fields, Class<?> type) {
		if(type.getSuperclass() != null) {
			getAllNonStaticFields(fields, type.getSuperclass());
		}
		fields.addAll(Arrays.asList(type.getDeclaredFields()).stream()
			.filter(x -> !Modifier.isStatic(x.getModifiers())).collect(Collectors.toList()));

		return fields;
	}

	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		if(type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		return fields;
	}

	public static String getClassNameFromType(Type type) {
		return type.toString().replaceAll("class ", "").replaceAll("interface ", "");
	}

	/**
	 * Explicitly sets the field accessible if necessary.
	 *
	 * @param field
	 *            the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if((!Modifier.isPublic(field.getModifiers()) ||
			!Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
			Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * Explicitly sets the method accessible if necessary.
	 *
	 * @param method
	 *            the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	public static void makeAccessible(Method method) {
		if((!Modifier.isPublic(method.getModifiers()) ||
			!Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}
}
