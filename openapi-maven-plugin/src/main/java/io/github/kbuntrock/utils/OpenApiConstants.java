package io.github.kbuntrock.utils;

public class OpenApiConstants {

	public static final String OBJECT_REFERENCE_PREFIX = "#/components/schemas/";
	public static final String OBJECT_REFERENCE_DECLARATION = "$ref";
	public static final String TYPE = "type";

	/**
	 * Elements in the "components" section, except the "schemas" (ordered by the specification : https://swagger.io/docs/specification/components/)
	 */
	public static final String[] COMPONENTS_STRUCTURE = {"parameters", "securitySchemes", "requestBodies", "responses", "headers",
		"examples", "links", "callbacks"};

	/**
	 * Schemas sub-section in "components" section
	 */
	public static final String SCHEMAS = "schemas";
}
