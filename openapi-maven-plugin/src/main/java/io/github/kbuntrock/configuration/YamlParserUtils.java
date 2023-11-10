package io.github.kbuntrock.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kévin Buntrock
 */
public class YamlParserUtils {

	private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));

	public static JsonNode readResourceFile(final String path) {
		try(final InputStream in = YamlParserUtils.class.getResourceAsStream(path)) {
			return mapper.readTree(in);
		} catch(final IOException e) {
			throw new RuntimeException("Impossible to read yml resource from path " + path, e);
		}
	}

}
