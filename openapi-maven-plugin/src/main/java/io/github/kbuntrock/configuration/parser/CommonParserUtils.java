package io.github.kbuntrock.configuration.parser;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.MojoRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Utilities to read configuration snippets from files or inline text and parse them as JSON/YAML.
 *
 * @author Kévin Buntrock
 */
public final class CommonParserUtils {

	private CommonParserUtils() {
		// Nothing to do
	}

	/**
	 * Resolve content from a file path relative to the Maven project base directory or treat the input as literal content when
	 * not a valid file path.
	 *
	 * @param mavenProject
	 *            the current Maven project
	 * @param input
	 *            a relative file path or the literal content
	 * @return the resolved content string (UTF-8), or {@code null} if input is empty
	 */
	public static String getContentFromFileOrText(final MavenProject mavenProject, final String input) {
		if(StringUtils.isEmpty(input)) {
			return null;
		}

		String content = input;

		// We can load a file if the attribute represent a path
		final String url = mavenProject.getBasedir() + FileSystems.getDefault().getSeparator() + input;
		Path path = null;
		try {
			path = Paths.get(url);
		} catch(final Exception ex) {
			// non parseable path. Probably not a path
		}

		if(path != null && path.toFile().exists() && !path.toFile().isDirectory()) {
			try {
				final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
				final StringBuilder sb = new StringBuilder();
				for(final String line : lines) {
					sb.append(line);
				}
				content = sb.toString();
			} catch(final IOException e) {
				throw new MojoRuntimeException("Cannot read content from file", e);
			}
		}
		return content;
	}

	public static Optional<JsonNode> parse(final MavenProject mavenProject, final String input) {
		// Yaml parsing is only available when parsing a file. Direct content is always in json (yml and xml in pom do not mix well)
		final boolean isYaml = input.endsWith(".yml") || input.endsWith(".yaml");
		final String content = getContentFromFileOrText(mavenProject, input);
		if(isYaml) {
			return Optional.ofNullable(YamlParserUtils.readFile(
				mavenProject.getBasedir() + FileSystems.getDefault().getSeparator() + input));
		}
		return JsonParserUtils.parse(content);
	}

}
