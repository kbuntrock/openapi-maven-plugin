package io.github.kbuntrock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class AbstractTest {

	private static final Log testLogger = new TestLogger();

	@BeforeAll
	public static void initTestClass() {
		Logger.INSTANCE.setLogger(testLogger);
		ReflectionsUtils.initiateTestMode();
	}

	@BeforeEach
	public void initTest() {
		JavadocMap.INSTANCE.setJavadocMap(new HashMap<>());
	}

	protected void checkGenerationResult(final String expectedFilePath, final File generatedFile) throws IOException {
		final InputStream expected = this.getClass().getClassLoader().getResourceAsStream(expectedFilePath);
		assertThat(new FileInputStream(generatedFile)).hasSameContentAs(expected);
	}

	protected void checkGenerationResult(List<File> generatedFiles) throws IOException, MojoFailureException, MojoExecutionException {

		if (generatedFiles.size() == 1) {
			Approvals.verify(FileUtils.readFileToString(generatedFiles.get(0), UTF_8));
		} else {
			generatedFiles.forEach(file -> {
				try {
					Approvals.verify(FileUtils.readFileToString(file, UTF_8), Approvals.NAMES.withParameters(file.getName()));
				} catch(IOException e) {
					throw new AssertionError(e);
				}
			});
		}
	}

	protected void checkGenerationResult(final File expectedFile, final File generatedFile) throws IOException {
		assertThat(generatedFile).hasSameTextualContentAs(expectedFile);
	}


	public static class TestLogger implements Log {

		@Override
		public boolean isDebugEnabled() {
			return false;
		}

		@Override
		public void debug(final CharSequence content) {

		}

		@Override
		public void debug(final CharSequence content, final Throwable error) {

		}

		@Override
		public void debug(final Throwable error) {

		}

		@Override
		public boolean isInfoEnabled() {
			return false;
		}

		@Override
		public void info(final CharSequence content) {

		}

		@Override
		public void info(final CharSequence content, final Throwable error) {

		}

		@Override
		public void info(final Throwable error) {

		}

		@Override
		public boolean isWarnEnabled() {
			return false;
		}

		@Override
		public void warn(final CharSequence content) {

		}

		@Override
		public void warn(final CharSequence content, final Throwable error) {

		}

		@Override
		public void warn(final Throwable error) {

		}

		@Override
		public boolean isErrorEnabled() {
			return false;
		}

		@Override
		public void error(final CharSequence content) {

		}

		@Override
		public void error(final CharSequence content, final Throwable error) {

		}

		@Override
		public void error(final Throwable error) {

		}
	}
}
