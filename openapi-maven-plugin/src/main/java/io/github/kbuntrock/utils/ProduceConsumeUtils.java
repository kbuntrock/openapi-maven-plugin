package io.github.kbuntrock.utils;

import io.github.kbuntrock.model.DataObject;
import org.springframework.http.MediaType;

public class ProduceConsumeUtils {

	private ProduceConsumeUtils() {
	}

	public static String getDefaultValue(final DataObject dataObject) {
		if(dataObject.getJavaClass().isEnum()) {
			// java enums are considered as a string in openapi type
			return MediaType.APPLICATION_JSON_VALUE;
		} else if(OpenApiDataType.STRING == dataObject.getOpenApiResolvedType().getType()) {
			return MediaType.TEXT_PLAIN_VALUE;
		} else {
			return MediaType.APPLICATION_JSON_VALUE;
		}
	}
}
