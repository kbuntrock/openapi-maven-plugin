package io.github.kbuntrock.it;

import com.soebes.itf.extension.assertj.MavenExecutionResultAssert;
import com.soebes.itf.jupiter.extension.*;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * @author Kevin Buntrock
 */
@MavenJupiterExtension
@MavenPredefinedRepository
public class BasicIT {

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk8(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "1.8", "-jdk8", "3.9");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk11(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "11", "-jdk11", "3.9");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk17(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "17", "-jdk17", "3.9");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk21(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "21", "-jdk21", "3.9");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk25(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "25", "-jdk25", "3.9");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk17_maven4(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "17", "-jdk17", "4.0");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk21_maven4(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "21", "-jdk21", "4.0");
	}

	@MavenTest
	@MavenGoal("install")
	@MavenOption(MavenCLIOptions.DEBUG)
	void nominal_test_case_jdk25_maven4(MavenExecutionResult result) throws IOException {
		nominal_test_case(result, "25", "-jdk25", "4.0");
	}

	private void nominal_test_case(MavenExecutionResult result, final String expectedJavaVersion, final String suffix,
		final String expectedMavenVersion) throws IOException {
		MavenExecutionResultAssert resultAssert = assertThat(result);
		if("17".equals(expectedJavaVersion)) {
			resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 3 operations generated.");
		} else if("21".equals(expectedJavaVersion) || "25".equals(expectedJavaVersion)) {
			resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 4 operations generated.");
		} else {
			resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 2 operations generated.");
		}

		String version = System.getProperty("java.version");
		Assertions.assertTrue(version.startsWith(expectedJavaVersion),
			"Java version does not match. Expected : " + expectedJavaVersion + ", got : " + version);
		resultAssert.out().debug().contains("Running on java " + version);

		resultAssert.out().debug().anyMatch(s -> s.startsWith("Running on maven " + expectedMavenVersion));

		File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
		File generatedFile = new File(target, "spec-open-api.yml");
		Assertions.assertTrue(target.exists());
		Assertions.assertTrue(generatedFile.exists());

		File m2Directory = result.getMavenProjectResult().getTargetCacheDirectory().toFile();
		File generatedArtifactFile = new File(m2Directory, "/io/github/kbuntrock/openapi/it/openapi-basic-it" + suffix
			+ "/23.5.2/openapi-basic-it" + suffix + "-23.5.2-spec-open-api.yml");
		Assertions.assertTrue(generatedArtifactFile.exists());

		try(InputStream generatedFileStream = new FileInputStream(generatedArtifactFile);
			InputStream resourceFileStream = BasicIT.class.getClassLoader()
				.getResourceAsStream("it/BasicIT/nominal_test_case" + suffix + ".yml")) {
			assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
		}
	}

}