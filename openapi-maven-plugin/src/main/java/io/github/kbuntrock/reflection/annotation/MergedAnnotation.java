package io.github.kbuntrock.reflection.annotation;

import java.util.Optional;

public interface MergedAnnotation {

	boolean isPresent();

	String getString(String attributeName);

	String[] getStringArray(String attributeName);

	Class<?> getClass(String attributeName);

	boolean getBoolean(String attributeName);

	int getInt(String attributeName);

	String[] getEnumArrayAsString(String attributeName);

	MergedAnnotation[] getAnnotationArray(String attributeName);

	MergedAnnotation getAnnotation(String attributeName);

	Optional<Object> getValue(String attributeName);
}
