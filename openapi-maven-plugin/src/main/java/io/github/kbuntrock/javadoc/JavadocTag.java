package io.github.kbuntrock.javadoc;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public enum JavadocTag {
	// Tags with extractable information (content will be stripped from the general description)
	SUMMARY,
	AUTHOR,

	// Formatting tags (text content should not be stripped)
	CODE,
	LINK,
	SEE;

	private static final HashSet<JavadocTag> formattingTags = new HashSet<>();

	static {
		formattingTags.add(CODE);
		formattingTags.add(LINK);
		formattingTags.add(SEE);
	}

    public static JavadocTag fromString(String tagName) {
        return JavadocTag.valueOf(tagName.toUpperCase(Locale.ROOT));
    }

	public static boolean isFormattingTag(String tagName) {
		try {
			JavadocTag tag = fromString(tagName);
			return isFormattingTag(tag);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static boolean isFormattingTag(JavadocTag tag) {
		return formattingTags.contains(tag);
	}

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
