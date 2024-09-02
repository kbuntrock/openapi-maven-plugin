package io.github.kbuntrock.it;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import com.soebes.itf.extension.assertj.MavenExecutionResultAssert;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;

/**
 * @author Kevin Buntrock
 */
@MavenJupiterExtension
public class BasicIT {

	@MavenTest
	@MavenGoal("install")
	public void nominal_test_case(final MavenExecutionResult result) throws IOException {
		final MavenExecutionResultAssert resultAssert = assertThat(result);
		resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 2 operations generated.");

		final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
		final File generatedFile = new File(target, "spec-open-api.yml");
		final File generatedFile2 = new File(target, "spec-open-api-impl.yml");
		Assertions.assertTrue(target.exists());
		Assertions.assertTrue(generatedFile.exists());
		Assertions.assertTrue(generatedFile2.exists());

		final File m2Directory = result.getMavenProjectResult().getTargetCacheDirectory().toFile();
		final File generatedArtifactFile = new File(m2Directory,
			"/io/github/kbuntrock/openapi/it/openapi-basic-it/23.5.2/openapi-basic-it-23.5.2-spec-open-api.yml");
		final File generatedArtifactFile2 = new File(m2Directory,
			"/io/github/kbuntrock/openapi/it/openapi-basic-it/23.5.2/openapi-basic-it-23.5.2-spec-open-api-impl.yml");
		Assertions.assertTrue(generatedArtifactFile.exists());
		Assertions.assertTrue(generatedArtifactFile2.exists());

		try(final InputStream generatedFileStream = new FileInputStream(generatedArtifactFile);
			final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/BasicIT/nominal_test_case.yml")) {
			assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
		}

		try(final InputStream generatedFileStream = new FileInputStream(generatedArtifactFile2);
			final InputStream resourceFileStream = BasicIT.class.getClassLoader()
				.getResourceAsStream("it/BasicIT/nominal_test_case_impl.yml")) {
			assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
		}
	}

	@MavenTest
	@MavenGoal("install")
	public void nominal_test_case_jaxrs(final MavenExecutionResult result) throws IOException {
		final MavenExecutionResultAssert resultAssert = assertThat(result);
		resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 2 operations generated.");

		final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
		final File generatedFile = new File(target, "spec-open-api.yml");
		Assertions.assertTrue(target.exists());
		Assertions.assertTrue(generatedFile.exists());

		final File m2Directory = result.getMavenProjectResult().getTargetCacheDirectory().toFile();
		final File generatedArtifactFile = new File(m2Directory,
			"/io/github/kbuntrock/openapi/it/openapi-basic-it-jaxrs/23.5.2/openapi-basic-it-jaxrs-23.5.2-spec-open-api.yml");
		Assertions.assertTrue(generatedArtifactFile.exists());

		try(final InputStream generatedFileStream = new FileInputStream(generatedArtifactFile);
			final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/BasicIT/nominal_test_case_jaxrs" +
				".yml")) {
			assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
		}

	}

	@MavenTest
	@MavenGoal("install")
	public void sealed_class(final MavenExecutionResult result) throws IOException {
		final MavenExecutionResultAssert resultAssert = assertThat(result);
		resultAssert.isSuccessful().out().info().contains("spec-open-api.yml : 1 tags and 2 operations generated.");

		final File target = new File(result.getMavenProjectResult().getTargetProjectDirectory().toFile(), "target");
		final File generatedFile = new File(target, "spec-open-api.yml");
		Assertions.assertTrue(target.exists());
		Assertions.assertTrue(generatedFile.exists());

		final File m2Directory = result.getMavenProjectResult().getTargetCacheDirectory().toFile();
		final File generatedArtifactFile = new File(m2Directory,
			"/io/github/kbuntrock/openapi/it/openapi-sealed-class-it/1.0.0/openapi-sealed-class-it-1.0.0-spec-open-api.yml");
		Assertions.assertTrue(generatedArtifactFile.exists());

		try(final InputStream generatedFileStream = new FileInputStream(generatedArtifactFile);
			final InputStream resourceFileStream = BasicIT.class.getClassLoader().getResourceAsStream("it/BasicIT/sealed_class" +
				".yml")) {
			assertThat(generatedFileStream).hasSameContentAs(resourceFileStream);
		}
	}

}