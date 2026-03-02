package io.github.kbuntrock.resources.dto.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a dto representing an order
 */
public class ResumeJacksonDto {

	/**
	 * Account of the resume (write only)
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private AccountJacksonDto account;

	/**
	 * Description of the resume
	 */
	private String description;

	public AccountJacksonDto getAccount() {
		return account;
	}

	public void setAccount(AccountJacksonDto account) {
		this.account = account;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
