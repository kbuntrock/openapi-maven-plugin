package io.github.kbuntrock.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kbuntrock.MojoRuntimeException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

/**
 * @author Kévin Buntrock
 */
public final class JsonConfigurationParserUtils {

	private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

	private JsonConfigurationParserUtils() {
		// Cannot be instanciated
	}

	/**
	 * Parse a configuration value representing a json document
	 *
	 * @param mavenProject
	 * @param fieldValue   Can represent a path to a json file, or directly a json string
	 * @return the parsed json node
	 */
	public static Optional<JsonNode> parse(final MavenProject mavenProject, final String fieldValue) {

		if(StringUtils.isEmpty(fieldValue)) {
			return Optional.empty();
		}

		String content = fieldValue;

		// We can load a free field file if the attribute represent a path
		final String url = mavenProject.getBasedir() + FileSystems.getDefault().getSeparator() + fieldValue;
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
				throw new MojoRuntimeException("Cannot reading free fields json configuration from file", e);
			}
		}
		try {
			return Optional.ofNullable(jsonObjectMapper.readTree(content));
		} catch(final JsonProcessingException e) {
			throw new MojoRuntimeException("Free fields json configuration cannot be parsed", e);
		}
	}

}
