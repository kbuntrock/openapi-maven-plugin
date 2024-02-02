package io.github.kbuntrock.json;

import io.github.kbuntrock.configuration.parser.JsonParserUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests on the json merging functionnality
 *
 * @author Kévin Buntrock
 */
public class JsonMergingTest {

	private String readFile(final String path) throws IOException {

		final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);

		return new BufferedReader(
			new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			.lines()
			.collect(Collectors.joining("\n"));
	}

	protected void checkFileStringEquality(final String expectedFilePath, final String generatedString) throws IOException {
		final String expected = readFile(expectedFilePath);
		Assertions.assertEquals(expected, generatedString);
	}

	@Test
	public void titleOverride() throws IOException {

		final String baseContent = readFile("ut/json/merging/base_file.json");
		final String toMergeContent = readFile("ut/json/merging/title/to_merge.json");

		final String merged = JsonParserUtils.merge(baseContent, toMergeContent);
		checkFileStringEquality("ut/json/merging/title/merged.json", merged);

	}

	@Test
	public void serverOverride() throws IOException {

		final String baseContent = readFile("ut/json/merging/base_file.json");
		final String toMergeContent = readFile("ut/json/merging/server_full/to_merge.json");

		final String merged = JsonParserUtils.merge(baseContent, toMergeContent);
		checkFileStringEquality("ut/json/merging/server_full/merged.json", merged);
	}

	@Test
	public void nonExistingContact() throws IOException {

		final String baseContent = readFile("ut/json/merging/base_file.json");
		final String toMergeContent = readFile("ut/json/merging/non_existing_contact/to_merge.json");

		final String merged = JsonParserUtils.merge(baseContent, toMergeContent);
		checkFileStringEquality("ut/json/merging/non_existing_contact/merged.json", merged);
	}

	@Test
	public void semiExistingContact() throws IOException {

		final String baseContent = readFile("ut/json/merging/semi_existing_contact/base_file.json");
		final String toMergeContent = readFile("ut/json/merging/semi_existing_contact/to_merge.json");

		final String merged = JsonParserUtils.merge(baseContent, toMergeContent);
		checkFileStringEquality("ut/json/merging/semi_existing_contact/merged.json", merged);
	}


}
