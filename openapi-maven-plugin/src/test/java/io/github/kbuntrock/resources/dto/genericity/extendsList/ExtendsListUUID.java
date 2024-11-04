package io.github.kbuntrock.resources.dto.genericity.extendsList;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A list of uuids
 */
public class ExtendsListUUID extends ArrayList<UUID> {

	/**
	 * Since this class is a list, this attribute is ignored
	 */
	private String someUnusedString;

	public String getSomeUnusedString() {
		return someUnusedString;
	}

	public void setSomeUnusedString(String someUnusedString) {
		this.someUnusedString = someUnusedString;
	}
}
