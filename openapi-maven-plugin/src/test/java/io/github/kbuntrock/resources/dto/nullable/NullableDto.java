package io.github.kbuntrock.resources.dto.nullable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class NullableDto {

	public String defaultValue;

	@NotNull
	public String notNullableValue;

	@Nullable
	public String nullableValue;

	@MyNotNull
	public String myNotNull;

	@MyNullable
	public String myNullable;

}
