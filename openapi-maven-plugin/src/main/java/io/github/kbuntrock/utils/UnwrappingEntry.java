package io.github.kbuntrock.utils;

/**
 * Represent an "unwrapping" entry (ex: java.util.Optional is unwrapped to its property "T value")
 *
 * @author Kévin Buntrock
 */
public class UnwrappingEntry {

	/**
	 * Class to unwrap
	 */
	private final Class<?> clazz;

	/**
	 * Name of the generic type to unwrap (ex: T in java.util.Optional)
	 */
	private String typeName;

	/**
	 * Can be null if not relevant for the entry
	 */
	private Boolean required;

	public UnwrappingEntry(final Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(final Boolean required) {
		this.required = required;
	}
}
