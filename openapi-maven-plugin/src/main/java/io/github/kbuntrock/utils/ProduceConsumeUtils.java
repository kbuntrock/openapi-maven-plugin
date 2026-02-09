package io.github.kbuntrock.utils;

import io.github.kbuntrock.model.DataObject;

/**
 * Utilities to determine default media types for request/response content based on the resolved OpenAPI data type.
 */
public class ProduceConsumeUtils {

	/** Default JSON content type. */
	public static final String APPLICATION_JSON_VALUE = "application/json";
	/** Default plain text content type for simple string payloads. */
	public static final String TEXT_PLAIN_VALUE = "text/plain";

	private ProduceConsumeUtils() {
	}

	/**
	 * Compute a sensible default media type for the given data object.
	 * <p>
	 * Rules:
	 * - Enums are represented as strings but commonly serialized as JSON
	 * - Explicit string types favor {@code text/plain}
	 * - Everything else falls back to {@code application/json}
	 * </p>
	 *
	 * @param dataObject
	 *            the data object to inspect
	 * @return a default media type (e.g., {@code application/json})
	 */
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
