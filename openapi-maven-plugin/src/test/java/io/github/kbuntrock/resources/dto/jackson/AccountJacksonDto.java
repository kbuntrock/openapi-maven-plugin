package io.github.kbuntrock.resources.dto.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * This is a dto representing an account
 */
public class AccountJacksonDto {

	/**
	 * id is Serialization only: returned in JSON, ignored on input
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	/**
	 * email is a regular field (read/write)
	 */
	private String email;

	/**
	 * password is deserialization only: accepted on input, never returned
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	/**
	 * Serialization only via JsonProperty access annotation
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant createdAt;

	private String registrationTokenInternal;

	/**
	 * getEmailDomain is a computed property: serialization only (no setter)
	 *
	 * @return the email domain part
	 */
	public String getEmailDomain() {
		// Implementation is not relevant
		return null;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * registrationToken is deserialization only via its setter (e.g., creation token)
	 */
	public void setRegistrationToken(String registrationToken) {
		this.registrationTokenInternal = registrationToken;
	}
}
