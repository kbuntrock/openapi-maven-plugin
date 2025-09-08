package io.github.kbuntrock.resources.dto.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represent some countries
 */
public enum EnumFieldAsValue {
	/**
	 * This is the France value
	 */
	FRANCE(1000, "France"),
	/**
	 * This is the Germany value
	 */
	GERMANY(2000, "Germany");

	/**
	 * Get the code of the country
	 */
	@JsonValue
	private final int code;
	private final String description;

	EnumFieldAsValue(final int code, final String description) {
		this.code = code;
		this.description = description;
	}
}
