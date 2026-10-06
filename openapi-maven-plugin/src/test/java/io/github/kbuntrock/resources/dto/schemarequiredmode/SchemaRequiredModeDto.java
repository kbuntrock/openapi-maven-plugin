package io.github.kbuntrock.resources.dto.schemarequiredmode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class SchemaRequiredModeDto {

	public String defaultValue;

	@NotBlank
	public String beanValidationOnly;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	public String explicitlyRequired;

	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	public String explicitlyNotRequired;

	@Schema(requiredMode = Schema.RequiredMode.AUTO)
	public String explicitlyAuto;

	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	@NotBlank
	public String requiredModeOverridesBeanValidation;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Both signals agree")
	@NotBlank
	public String bothSignalsAgree;

	@Schema(format = "email", description = "An explicit format the Java type alone can't express")
	public String explicitFormat;

}
