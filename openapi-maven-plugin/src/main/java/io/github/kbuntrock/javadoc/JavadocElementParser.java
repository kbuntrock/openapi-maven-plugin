package io.github.kbuntrock.javadoc;

import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.javadoc.description.JavadocDescriptionElement;
import com.github.javaparser.javadoc.description.JavadocInlineTag;
import com.github.javaparser.javadoc.description.JavadocSnippet;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavadocElementParser {

	public static final Pattern hrefLinkRegex = Pattern.compile("href=\"(.*?)\"");

	public static Optional<String> getSummary(JavadocDescription description, String endOfLineReplacement) {
		return processJavadocElements(description, endOfLineReplacement, elements -> elements
			.filter(onlyTagsOfType(JavadocTag.SUMMARY))
			.map(JavadocElementParser::toTagContent));
	}

	public static Optional<String> getDescription(JavadocDescription description, String endOfLineReplacement1) {
		return processJavadocElements(description, endOfLineReplacement1, elements -> elements
			.filter(JavadocElementParser::onlySnippetsAndFormattingTags)
			.map(JavadocElementParser::formatDescriptionElement));
	}

	private static Optional<String> processJavadocElements(
		JavadocDescription description,
		String endOfLineReplacement,
		Function<Stream<JavadocDescriptionElement>, Stream<String>> elementProcessor) {
		return Optional.ofNullable(description)
			.map(JavadocDescription::getElements)
			.map(Collection::stream)
			.map(elementProcessor)
			.map(s -> s.collect(Collectors.joining()))
			.map(s -> s.replaceAll("\r\n", "\n"))
			.map(s -> removeNewlinesIfActivated(s, endOfLineReplacement))
			.filter(text -> !text.isEmpty())
			.map(String::trim);
	}

	private static String formatDescriptionElement(JavadocDescriptionElement javadocElement) {
		if(javadocElement instanceof JavadocInlineTag) {
			JavadocInlineTag tag = (JavadocInlineTag) javadocElement;
			switch(JavadocTag.fromString(tag.getName())) {
				case CODE:
					return toInlineCodeMarkdown(tag);
				case SEE:
					return toEmphasizedMarkdown(tag);
				case LINK:
					return toHrefMarkdown(tag);
				default:
					return tag.getContent();
			}
		}

		return javadocElement.toText();
	}

	private static String toInlineCodeMarkdown(JavadocInlineTag tag) {
		return "`" + tag.getContent().trim() + "`";
	}

	private static String toEmphasizedMarkdown(JavadocInlineTag tag) {
		return "*" + tag.getContent().trim() + "*";
	}

	private static String toHrefMarkdown(JavadocInlineTag tag) {
		Matcher m = hrefLinkRegex.matcher(tag.getContent());
		String url = null;
		if(m.find()) {
			url = m.group(1);
		}
		return String.format("[%s](%s)", url, url);
	}

	private static Predicate<JavadocDescriptionElement> onlyTagsOfType(JavadocTag tag) {
		return e -> (e instanceof JavadocInlineTag && tag.toString().toLowerCase().equals(((JavadocInlineTag) e).getName()));
	}

	private static boolean onlySnippets(JavadocDescriptionElement e) {
		return e instanceof JavadocSnippet;
	}

	private static String toTagContent(JavadocDescriptionElement t) {
		return ((JavadocInlineTag) t).getContent().trim();
	}

	private static boolean onlySnippetsAndFormattingTags(JavadocDescriptionElement e) {
		return onlySnippets(e) || (
			e instanceof JavadocInlineTag &&
				JavadocTag.isFormattingTag(((JavadocInlineTag) e).getName()));
	}

	private static String removeNewlinesIfActivated(String text, String endOfLineReplacement) {
		return endOfLineReplacement != null
			? text.replaceAll("\\r\\n", endOfLineReplacement).replaceAll("\\n", endOfLineReplacement)
			: text;
	}
}
