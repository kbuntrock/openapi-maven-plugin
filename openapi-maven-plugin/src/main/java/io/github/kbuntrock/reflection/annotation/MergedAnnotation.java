package io.github.kbuntrock.reflection.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

public class MergedAnnotation {

	private final Object mergedAnnotation;
	private final MergeAnnotationsHelper helper;

	MergedAnnotation(Object mergedAnnotation, MergeAnnotationsHelper helper) {
		this.mergedAnnotation = mergedAnnotation;
		this.helper = helper;
	}

	public boolean isPresent() {
		return helper.invokeWrapping("MergedAnnotation#isPresent",
			() -> (boolean) helper.methodIsPresent.invoke(mergedAnnotation));
	}

	public String getString(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getString",
			() -> (String) helper.methodGetString.invoke(mergedAnnotation, attributeName));
	}

	public String[] getStringArray(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getStringArray",
			() -> (String[]) helper.methodGetStringArray.invoke(mergedAnnotation, attributeName));
	}

	public Class<?> getClass(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getClass",
			() -> (Class<?>) helper.methodGetClass.invoke(mergedAnnotation, attributeName));
	}

	public boolean getBoolean(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getBoolean",
			() -> (boolean) helper.methodGetBoolean.invoke(mergedAnnotation, attributeName));
	}

	public int getInt(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getInt",
			() -> (int) helper.methodGetInt.invoke(mergedAnnotation, attributeName));
	}

	public String[] getEnumArrayAsString(String attributeName) {
		Enum[] enumerations = helper.invokeWrapping("MergedAnnotation#getEnumArray",
			() -> (Enum[]) helper.methodGetEnumArray.invoke(mergedAnnotation, attributeName, Enum.class));
		return Arrays.stream(enumerations).map(Enum::name).toArray(String[]::new);
	}

	public MergedAnnotation[] getAnnotationArray(String attributeName) {
		Object[] result = helper.invokeWrapping("MergedAnnotation#getAnnotationArray",
			() -> (Object[]) helper.methodGetAnnotationArray.invoke(mergedAnnotation, attributeName, Annotation.class));
		return Arrays.stream(result).map(r -> new MergedAnnotation(r, helper)).toArray(MergedAnnotation[]::new);
	}

	public MergedAnnotation getAnnotation(String attributeName) {
		Object result = helper.invokeWrapping("MergedAnnotation#getAnnotation",
			() -> helper.methodGetAnnotation.invoke(mergedAnnotation, attributeName, Annotation.class));
		return new MergedAnnotation(result, helper);
	}

	public Optional<Object> getValue(String attributeName) {
		return helper.invokeWrapping("MergedAnnotation#getValue",
			() -> (Optional<Object>) helper.methodGetValue.invoke(mergedAnnotation, attributeName));
	}

}
