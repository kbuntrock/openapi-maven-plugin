package io.github.kbuntrock.resources.dto.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumAsValueFunctionPrecedence {
	FRANCE(1000),
	GERMANY(2000);

	@JsonValue
	private final int code;

	EnumAsValueFunctionPrecedence(final int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@JsonValue
	public int getNormalizedCode() {
		return (code/2)+3;
	}
}
