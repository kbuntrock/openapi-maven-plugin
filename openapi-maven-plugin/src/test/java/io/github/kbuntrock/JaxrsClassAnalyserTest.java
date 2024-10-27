package io.github.kbuntrock;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.resources.endpoint.account.AccountJakartaController;
import io.github.kbuntrock.resources.endpoint.account.AccountJaxrsController;
import io.github.kbuntrock.resources.endpoint.jaxrs.ResponseJaxrsController;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

public class JaxrsClassAnalyserTest extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("My Project");
		mavenProjet.setVersion("10.5.36");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	private DocumentationMojo createBasicMojo(final String apiLocation) {
		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setLibrary(Library.JAVAX_RS.name());
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Collections.singletonList(apiLocation));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());
		return mojo;
	}

	@Test
	public void jaxrs_basic() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(AccountJaxrsController.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void jakarta_basic() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(AccountJakartaController.class.getCanonicalName());
		mojo.getApis().get(0).setLibrary(Library.JAKARTA_RS.name());
		final Substitution substitution = new Substitution();
		substitution.setSubstitute("AccountJaxrs");
		substitution.setRegex("^AccountJakarta");
		mojo.getApis().get(0).getTag().setSubstitutions(Arrays.asList(substitution));
		mojo.getApis().get(0).setOperationId("{tag_name}.{method_name}");

		List<File> generatedFiles = mojo.documentProject();
		checkGenerationResult(generatedFiles);
		// The result should be exactly the same beetween the jaxrs or the jakarta rs generation thanks to the configured "substitutions"
		List<File> jaxrsGeneratedFiles = createBasicMojo(AccountJaxrsController.class.getCanonicalName()).documentProject();
		assertThat(generatedFiles.get(0)).hasSameTextualContentAs(jaxrsGeneratedFiles.get(0));
	}

	@Test
	public void response_jaxrs_javax() throws MojoFailureException, MojoExecutionException, IOException {

		DocumentationMojo mojo = createBasicMojo(ResponseJaxrsController.class.getCanonicalName());
		mojo.getApis().get(0).setLibrary(Library.JAVAX_RS.name());
		mojo.getApiConfiguration().setCustomResponseTypeAnnotation("io.github.kbuntrock.resources.endpoint.jaxrs.ResponseType");

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void response_jaxrs_jakarta() throws MojoFailureException, MojoExecutionException, IOException {

		DocumentationMojo mojo = createBasicMojo(ResponseJaxrsController.class.getCanonicalName());
		mojo.getApis().get(0).setLibrary(Library.JAKARTA_RS.name());
		mojo.getApiConfiguration().setCustomResponseTypeAnnotation("io.github.kbuntrock.resources.endpoint.jaxrs.ResponseType");

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void optional_unmapping_jaxrs() throws IOException, MojoExecutionException, MojoFailureException {

		final DocumentationMojo mojo = createBasicMojo(
			io.github.kbuntrock.resources.endpoint.optional.object.OptionalController.class.getCanonicalName());
		mojo.getApis().get(0).setDefaultProduceConsumeGuessing(false);
		mojo.getApis().get(0).setOperationId("{method_name}");
		mojo.getApis().get(0).setLoopbackOperationName(false);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void optional_unmapping_jakarta() throws IOException, MojoExecutionException, MojoFailureException {

		final DocumentationMojo mojo = createBasicMojo(
			io.github.kbuntrock.resources.endpoint.optional.object.OptionalController.class.getCanonicalName());
		mojo.getApis().get(0).setLibrary(Library.JAKARTA_RS.name());
		mojo.getApis().get(0).setDefaultProduceConsumeGuessing(false);
		mojo.getApis().get(0).setOperationId("{method_name}");
		mojo.getApis().get(0).setLoopbackOperationName(false);

		checkGenerationResult(mojo.documentProject());
	}

}
