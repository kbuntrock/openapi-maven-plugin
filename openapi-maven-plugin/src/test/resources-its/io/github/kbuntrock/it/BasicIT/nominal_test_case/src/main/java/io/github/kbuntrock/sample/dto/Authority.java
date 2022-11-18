package io.github.kbuntrock.sample.dto;

/**
 * Permissions of a user
 */
public enum Authority {
	/**
	 * Has the right to access the application
	 */
	ACCESS_APP(1000),
	/**
	 * Can read user informations
	 */
	READ_USER(2000),
	/**
	 * Can update user informations
	 */
	UPDATE_USER(3000);

	private final int code;

	Authority(final int code) {
		this.code = code;
	}
}
