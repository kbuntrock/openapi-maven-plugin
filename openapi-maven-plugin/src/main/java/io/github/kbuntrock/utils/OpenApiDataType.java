package io.github.kbuntrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum OpenApiDataType {

	STRING,
	BOOLEAN,
	INTEGER,
	NUMBER,
	ARRAY,
	OBJECT;

	private static final Map<String, OpenApiDataType> map = new HashMap<>();

	static {
		for(final OpenApiDataType type : OpenApiDataType.values()) {
			map.put(type.toString().toLowerCase(Locale.ENGLISH), type);
		}
	}

	public static OpenApiDataType fromJsonNode(final JsonNode jsonNode) {
		final JsonNode typeNode = jsonNode.get("type");
		return map.getOrDefault(typeNode.asText(), OBJECT);
	}

}
