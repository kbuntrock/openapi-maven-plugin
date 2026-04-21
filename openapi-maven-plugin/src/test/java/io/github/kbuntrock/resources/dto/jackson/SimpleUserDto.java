package io.github.kbuntrock.resources.dto.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A simple user
 */
@JsonPropertyOrder({ "username", "isActive", "admin" })
public class SimpleUserDto {

	/**
	 * User firstname
	 */
	private String username;

	/**
	 * Whether the user is active or not
	 */
	@JsonProperty("isActive")
	private boolean active;

	/**
	 * Whether the user is an admin or not
	 */
	@JsonProperty
	private boolean admin;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
