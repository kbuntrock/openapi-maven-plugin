package io.github.kbuntrock.configuration.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kbuntrock.MojoRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Optional;

/**
 * JSON parsing and merging helpers for user-provided configuration snippets.
 */
public final class JsonParserUtils {

	public static final ObjectMapper jsonObjectMapper = new ObjectMapper();
	public static final String PRETTY_PRINT_LINE_BREAK = "\n";

	private JsonParserUtils() {
		// Cannot be instantiated
	}

	/**
	 * Parse a configuration value representing a JSON document.
	 *
	 * @param jsonContent
	 *            raw JSON content
	 * @return the parsed JSON node, or empty if the input is blank
	 */
	public static Optional<JsonNode> parse(final String jsonContent) {

		if(StringUtils.isEmpty(jsonContent)) {
			return Optional.empty();
		}

		try {
			return Optional.ofNullable(jsonObjectMapper.readTree(jsonContent));
		} catch(final JsonProcessingException e) {
			throw new MojoRuntimeException("json content cannot be parsed", e);
		}
	}

	public static String merge(final String baseJson, final String updateJson) {
		try {
			final JsonNode mutatedNode = jsonObjectMapper.readTree(baseJson);
			final JsonNode updateNode = jsonObjectMapper.readTree(updateJson);
			mergeInternal(mutatedNode, updateNode);
			return jsonObjectMapper.writer(new DefaultPrettyPrinter()
				.withObjectIndenter(new DefaultIndenter().withLinefeed(PRETTY_PRINT_LINE_BREAK))).writeValueAsString(mutatedNode);
		} catch(final JsonProcessingException e) {
			throw new RuntimeException("Cannot parse freefields entries", e);
		}
	}

	public static void mergeInternal(final JsonNode mutatedNode,
		final JsonNode updateNode) {

		final Iterator<String> fieldNames = updateNode.fieldNames();
		while(fieldNames.hasNext()) {

			final String fieldName = fieldNames.next();
			final JsonNode jsonNode = mutatedNode.get(fieldName);
			// if field exists and is an embedded object
			if(jsonNode != null && jsonNode.isObject()) {
				mergeInternal(jsonNode, updateNode.get(fieldName));
			} else if(mutatedNode instanceof ObjectNode) {
				// Overwrite field
				final JsonNode value = updateNode.get(fieldName);
				((ObjectNode) mutatedNode).set(fieldName, value);
			}
		}
	}

}
