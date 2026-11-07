package io.github.kbuntrock.resources.dto.jspecify;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class NoScopeDto {

	public String defaultValue;

	public @Nullable String nullableValue;

	public @NonNull String nonNullValue;

	private String nonNullOnGetterValue;

	private String nonNullOnSetterValue;

	public @NonNull String getNonNullOnGetterValue() {
		return nonNullOnGetterValue;
	}

	public void setNonNullOnGetterValue(String nonNullOnGetterValue) {
		this.nonNullOnGetterValue = nonNullOnGetterValue;
	}

	public String getNonNullOnSetterValue() {
		return nonNullOnSetterValue;
	}

	public void setNonNullOnSetterValue(@NonNull String nonNullOnSetterValue) {
		this.nonNullOnSetterValue = nonNullOnSetterValue;
	}
}
