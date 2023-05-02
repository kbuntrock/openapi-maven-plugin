package io.github.kbuntrock.resources.dto.collision;

import java.util.List;

/**
 * A very concise account representation
 *
 * @author Kévin Buntrock
 */
public class AccountDto {

	/**
	 * Nickname the user wants to be called by
	 */
	private String pseudo;

	/**
	 * List of authorities
	 */
	private List<Authority> authorityList;

	public enum Authority {
		ACCES_APP
	}

}
