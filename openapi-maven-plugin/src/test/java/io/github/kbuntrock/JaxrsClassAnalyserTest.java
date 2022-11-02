package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;
import io.github.kbuntrock.configuration.library.Library;
import io.github.kbuntrock.resources.endpoint.account.AccountJakartaController;
import io.github.kbuntrock.resources.endpoint.account.AccountJaxrsController;
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

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/JaxrsClassAnalyserTest/jaxrs_basic.yml", generated.get(0));
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

		final List<File> generated = mojo.documentProject();
		// The result should be exactly the same beetween the jaxrs or the jakarta rs generation thanks to the configured "substitutions"
		checkGenerationResult("ut/JaxrsClassAnalyserTest/jaxrs_basic.yml", generated.get(0));
	}

}
