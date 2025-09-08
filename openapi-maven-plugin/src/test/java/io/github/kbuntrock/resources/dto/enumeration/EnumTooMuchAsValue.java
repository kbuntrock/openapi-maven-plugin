package io.github.kbuntrock.resources.dto.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumTooMuchAsValue {
	FRANCE(1000),
	GERMANY(2000);

	private final int code;

	EnumTooMuchAsValue(final int code) {
		this.code = code;
	}

	@JsonValue
	public int getCode() {
		return code;
	}

	@JsonValue
	public int getNormalizedCode() {
		return code/2;
	}
}
