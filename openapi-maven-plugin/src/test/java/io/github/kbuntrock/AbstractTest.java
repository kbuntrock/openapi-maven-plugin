package io.github.kbuntrock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public class AbstractTest {

	@BeforeAll
	public static void initTestClass() {
		ReflectionsUtils.initiateTestMode();
	}

	@BeforeEach
	public void initTest() {
		// In order to see all logs during testing, uncomment this :
		// Logger.INSTANCE.setLogger(Mockito.spy(SystemStreamLog.class));
		Logger.INSTANCE.setLogger(Mockito.mock(Log.class));
	}

	protected void checkGenerationResult(List<File> generatedFiles) throws IOException{
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

}
