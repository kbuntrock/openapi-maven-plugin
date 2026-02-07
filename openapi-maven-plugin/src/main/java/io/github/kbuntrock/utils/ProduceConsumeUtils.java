package io.github.kbuntrock.utils;

import io.github.kbuntrock.model.DataObject;

public class ProduceConsumeUtils {

	public static final String APPLICATION_JSON_VALUE = "application/json";
	public static final String TEXT_PLAIN_VALUE = "text/plain";

	private ProduceConsumeUtils() {
	}

	public static String getDefaultValue(final DataObject dataObject) {
		if(dataObject.getJavaClass().isEnum()) {
			// java enums are considered as a string in openapi type
			return APPLICATION_JSON_VALUE;
		} else if(OpenApiDataType.STRING == dataObject.getOpenApiResolvedType().getType()) {
			return TEXT_PLAIN_VALUE;
		} else {
			return APPLICATION_JSON_VALUE;
		}
	}
}
