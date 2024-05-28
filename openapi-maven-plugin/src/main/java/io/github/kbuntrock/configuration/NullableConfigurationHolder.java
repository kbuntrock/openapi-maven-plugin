package io.github.kbuntrock.configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NullableConfigurationHolder {

	private static final String defaultJakartaNullable = "jakarta.annotation.Nullable";
	private static final String defaultJavaxNullable = "javax.annotation.Nullable";

	private static final String defaultJakartaNotNull = "jakarta.validation.constraints.NotNull";
	private static final String defaultJakartaNotBlank = "jakarta.validation.constraints.NotBlank";
	private static final String defaultJakartaNotEmpty = "jakarta.validation.constraints.NotEmpty";
	private static final String defaultJavaxNotNull = "javax.validation.constraints.NotNull";
	private static final String defaultJavaxNotBlank = "javax.validation.constraints.NotBlank";
	private static final String defaultJavaxNotEmpty = "javax.validation.constraints.NotEmpty";

	private static List<String> nullableAnnotations;

	private static List<String> nonNullAnnotations;

	private static boolean defaultNonNullableFields;

	public static void storeConfig(final CommonApiConfiguration commonApiConfiguration) {
		defaultNonNullableFields = commonApiConfiguration.defaultNonNullableFields != null
			&& commonApiConfiguration.defaultNonNullableFields;

		nullableAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nullableAnnotation != null) {
			nullableAnnotations.addAll(commonApiConfiguration.nullableAnnotation);
		} else {
			nullableAnnotations.add(defaultJakartaNullable);
			nullableAnnotations.add(defaultJavaxNullable);
		}
		nonNullAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nonNullableAnnotation != null) {
			nonNullAnnotations.addAll(commonApiConfiguration.nonNullableAnnotation);
		} else {
			nonNullAnnotations.add(defaultJakartaNotNull);
			nonNullAnnotations.add(defaultJakartaNotBlank);
			nonNullAnnotations.add(defaultJakartaNotEmpty);
			nonNullAnnotations.add(defaultJavaxNotNull);
			nonNullAnnotations.add(defaultJavaxNotBlank);
			nonNullAnnotations.add(defaultJavaxNotEmpty);
		}

	}

	private static List<String> getNullableAnnotations() {
		return nullableAnnotations;
	}

	private static List<String> getNonNullAnnotations() {
		return nonNullAnnotations;
	}

	public static boolean isDefaultNonNullableFields() {
		return defaultNonNullableFields;
	}

	public static boolean hasNullableAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNullableAnnotations().contains(name));
	}


	public static boolean hasNonNullAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNonNullAnnotations().contains(name));
	}
}
