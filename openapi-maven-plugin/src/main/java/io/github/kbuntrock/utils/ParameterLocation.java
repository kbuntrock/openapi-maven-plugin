package io.github.kbuntrock.utils;

/**
 * Location of an API parameter in an HTTP request.
 */
public enum ParameterLocation {
	PATH("path"),
	QUERY("query"),
	BODY("body"),
	HEADER("header"),
	BODY_PART("body_part"),
	COOKIE("cookie");

	/** Lower-case serialized value used in the OpenAPI output. */
	private final String value;

	ParameterLocation(String value) {
		if(value == null) {
			value = "";
		}
		this.value = value.trim().toLowerCase();
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	/**
	 * Parse a string into a {@link ParameterLocation}, defaulting to {@link #QUERY}
	 * on null or unknown input.
	 *
	 * @param value
	 *            input string (case-insensitive)
	 * @return matching {@link ParameterLocation} or {@link #QUERY} if none
	 */
	public static ParameterLocation fromValue(String value) {
		if(value == null) {
			return QUERY;
		}
		String normalized = value.trim().toLowerCase();
		for(ParameterLocation loc : values()) {
			if(loc.value.equals(normalized)) {
				return loc;
			}
		}
		return QUERY;
	}
}
