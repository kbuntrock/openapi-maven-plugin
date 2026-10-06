package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The success entity", example = "A John Doe user")
public class SuccessEntity {

	@Schema(description = "The first name", example = "John", examples = { "John", "Bob", "Francois" })
	private String firstName;
	@Schema(description = "The last name", example = "Doe", examples = { "Scott", "Civil" })
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
