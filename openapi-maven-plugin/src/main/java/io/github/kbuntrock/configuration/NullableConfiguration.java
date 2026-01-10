package io.github.kbuntrock.configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class NullableConfiguration {

	private static final String defaultJakartaNullable = "jakarta.annotation.Nullable";
	private static final String defaultJavaxNullable = "javax.annotation.Nullable";

	private static final String defaultJakartaNotNull = "jakarta.validation.constraints.NotNull";
	private static final String defaultJakartaNotBlank = "jakarta.validation.constraints.NotBlank";
	private static final String defaultJakartaNotEmpty = "jakarta.validation.constraints.NotEmpty";
	private static final String defaultJavaxNotNull = "javax.validation.constraints.NotNull";
	private static final String defaultJavaxNotBlank = "javax.validation.constraints.NotBlank";
	private static final String defaultJavaxNotEmpty = "javax.validation.constraints.NotEmpty";

	private final List<String> nullableAnnotations;
	private final List<String> nonNullAnnotations;
	private final boolean defaultNonNullableFields;

	public NullableConfiguration(final CommonApiConfiguration commonApiConfiguration) {
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

	private List<String> getNullableAnnotations() {
		return nullableAnnotations;
	}

	private List<String> getNonNullAnnotations() {
		return nonNullAnnotations;
	}

	public boolean isDefaultNonNullableFields() {
		return defaultNonNullableFields;
	}

	public boolean hasNullableAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNullableAnnotations().contains(name));
	}

	public boolean hasNonNullAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNonNullAnnotations().contains(name));
	}
}
