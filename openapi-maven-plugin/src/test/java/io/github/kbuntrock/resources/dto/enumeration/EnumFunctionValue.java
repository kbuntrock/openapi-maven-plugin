package io.github.kbuntrock.resources.dto.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumFunctionValue {
	FRANCE(1000),
	/**
	 * Das ist Deutschland
	 */
	GERMANY(2000);

	private final int code;

	EnumFunctionValue(final int code) {
		this.code = code;
	}

	@JsonValue
	public int getNormalizedCode() {
		return code/2;
	}
}
