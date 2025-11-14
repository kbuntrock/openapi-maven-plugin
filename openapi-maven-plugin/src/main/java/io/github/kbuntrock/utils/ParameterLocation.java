package io.github.kbuntrock.utils;

public enum ParameterLocation {
    PATH("path"),
    QUERY("query"),
    BODY("body"),
    HEADER("header"),
    BODY_PART("body_part"),
    COOKIE("cookie");

	private String value;

	ParameterLocation(String value) {
		if (value == null) value = "";
        this.value = value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public static ParameterLocation fromValue(String value) {
        if (value == null) {
            return QUERY;
        }
        String normalized = value.trim().toLowerCase();
        for (ParameterLocation loc : values()) {
            if (loc.value.equals(normalized)) {
                return loc;
            }
        }
        return QUERY;
    }
}
