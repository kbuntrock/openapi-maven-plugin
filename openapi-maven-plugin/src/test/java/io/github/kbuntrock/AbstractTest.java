package io.github.kbuntrock;

import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.util.DigestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
		InputStream expected = this.getClass().getClassLoader().getResourceAsStream(expectedFilePath);
		assertThat(new FileInputStream(generatedFile)).hasSameContentAs(expected);
	}

	protected void checkGenerationResult(final File expectedFile, final File generatedFile) throws IOException {
		assertThat(generatedFile).hasSameTextualContentAs(generatedFile);
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
