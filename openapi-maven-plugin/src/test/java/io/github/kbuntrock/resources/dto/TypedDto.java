package io.github.kbuntrock.resources.dto;

/**
 * @author Kevin Buntrock
 */
public class TypedDto<H> {

	private WrapperDto<H> wrapped;
	private AccountDto account;

	public WrapperDto<H> getWrapped() {
		return wrapped;
	}

	public AccountDto getAccount() {
		return account;
	}
}
