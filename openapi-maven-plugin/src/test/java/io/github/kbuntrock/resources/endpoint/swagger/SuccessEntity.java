package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The success entity", example = "A John Doe user")
public class SuccessEntity {

    @Schema(description = "The first name", example = "John")
    private String firstName;
    @Schema(description = "The last name", example = "Doe")
    private String lastName;
}
