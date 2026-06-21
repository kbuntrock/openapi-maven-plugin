package io.github.kbuntrock.resources.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO used by tests that verify {@code @Schema(description = ...)} on DTO fields
 * is propagated to OpenAPI query parameter descriptions, when the field has no
 * Javadoc to drive the description.
 */
public class SchemaDescriptionDto {

	@Schema(description = "Description coming from Schema annotation", example = "schemaValue")
	private String fieldFromSchemaOnly;

	/**
	 * Description coming from Javadoc
	 */
	private String fieldFromJavadocOnly;

	/**
	 * Description coming from Javadoc (wins)
	 */
	@Schema(description = "Description coming from Schema (loses)", example = "javadocFieldExample")
	private String fieldFromBothJavadocWins;

	@Schema(example = "exampleOnly")
	private String fieldWithExampleFromSchemaOnly;

	public String getFieldFromSchemaOnly() {
		return fieldFromSchemaOnly;
	}

	public void setFieldFromSchemaOnly(final String fieldFromSchemaOnly) {
		this.fieldFromSchemaOnly = fieldFromSchemaOnly;
	}

	public String getFieldFromJavadocOnly() {
		return fieldFromJavadocOnly;
	}

	public void setFieldFromJavadocOnly(final String fieldFromJavadocOnly) {
		this.fieldFromJavadocOnly = fieldFromJavadocOnly;
	}

	public String getFieldFromBothJavadocWins() {
		return fieldFromBothJavadocWins;
	}

	public void setFieldFromBothJavadocWins(final String fieldFromBothJavadocWins) {
		this.fieldFromBothJavadocWins = fieldFromBothJavadocWins;
	}

	public String getFieldWithExampleFromSchemaOnly() {
		return fieldWithExampleFromSchemaOnly;
	}

	public void setFieldWithExampleFromSchemaOnly(final String fieldWithExampleFromSchemaOnly) {
		this.fieldWithExampleFromSchemaOnly = fieldWithExampleFromSchemaOnly;
	}
}
