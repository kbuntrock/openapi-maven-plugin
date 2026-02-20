package io.github.kbuntrock.resources.dto.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a dto representing an order
 */
public class OrderJacksonDto {

	/**
	 * orderId is serialization only: emitted in JSON, ignored on input
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private AccountJacksonDto account;

	/**
	 * customerEmail is a regular field (read/write)
	 */
	private String customerEmail;

	/**
	 * paymentToken is a deserialization only: accepted on input, never emitted
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String paymentToken;

	public AccountJacksonDto getAccount() {
		return account;
	}

	public void setAccount(AccountJacksonDto account) {
		this.account = account;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getPaymentToken() {
		return paymentToken;
	}

	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}
}
