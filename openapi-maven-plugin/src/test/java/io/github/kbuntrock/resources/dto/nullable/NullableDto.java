package io.github.kbuntrock.resources.dto.nullable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class NullableDto {

    private String defaultValue;

    @NotNull
    private String notNullableValue;

    @Nullable
    private String nullableValue;

    @MyNotNull
    private String myNotNull;

    @MyNullable
    private String myNullable;

}
