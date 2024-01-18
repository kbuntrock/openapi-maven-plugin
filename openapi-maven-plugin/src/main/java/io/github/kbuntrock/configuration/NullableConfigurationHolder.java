package io.github.kbuntrock.configuration;

import java.util.ArrayList;
import java.util.List;

public class NullableConfigurationHolder {

	private static final String defaultJakartaNullable = "jakarta.annotation.Nullable";
	private static final String defaultJavaxNullable = "javax.annotation.Nullable";

	private static final String defaultJakartaNotNull = "jakarta.validation.constraints.NotNull";
	private static final String defaultJavaxNotNull = "javax.validation.constraints.NotNull";

	private static List<String> nullableAnnotations;

	private static List<String> nonNullAnnotations;

	private static boolean defaultNonNullableFields;

	public static void storeConfig(final CommonApiConfiguration commonApiConfiguration) {
		defaultNonNullableFields = commonApiConfiguration.defaultNonNullableFields != null
			&& commonApiConfiguration.defaultNonNullableFields;

		nullableAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nullableAnnotation != null) {
			nullableAnnotations.add(commonApiConfiguration.nullableAnnotation);
		} else {
			nullableAnnotations.add(defaultJakartaNullable);
			nullableAnnotations.add(defaultJavaxNullable);
		}
		nonNullAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nonNullableAnnotation != null) {
			nonNullAnnotations.add(commonApiConfiguration.nonNullableAnnotation);
		} else {
			nonNullAnnotations.add(defaultJakartaNotNull);
			nonNullAnnotations.add(defaultJavaxNotNull);
		}

	}

	public static List<String> getNullableAnnotations() {
		return nullableAnnotations;
	}

	public static List<String> getNonNullAnnotations() {
		return nonNullAnnotations;
	}

	public static boolean isDefaultNonNullableFields() {
		return defaultNonNullableFields;
	}
}
