package io.github.kbuntrock.utils;

public enum OpenApiDataFormat {
	NONE(null),
	FLOAT("float"),
	DOUBLE("double"),
	INT32("int32"),
	INT64("int64"),
	DATE("date"),
	DATE_TIME("date-time"),
	TIME("time"),
	BYTE("byte"),
	BINARY("binary"),
	EMAIL("email"),
	UUID("uuid"),
	UNKNOWN("unknow_format");

	private final String value;

	OpenApiDataFormat(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
