package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.spring.ResponseEntityController;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

public class ModelSubstitutionTest extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("My Project");
		mavenProjet.setVersion("10.5.36");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	@Test
	public void object_substitution() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setOpenapiModelsPath(
			"src/test/resources/ut/ModelSubstitutionTest/object_substitution/custom-openapi-model.yml");
		apiConfiguration.setModelsAssociationsPath(
			"src/test/resources/ut/ModelSubstitutionTest/object_substitution/custom-model-association.yml");
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Arrays.asList(ResponseEntityController.class.getCanonicalName()));
		apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/ModelSubstitutionTest/object_substitution/object_substitution.yml", generated.get(0));
	}

	@Test
	public void enum_substitution() throws MojoFailureException, IOException, MojoExecutionException {

		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setOpenapiModelsPath(
			"src/test/resources/ut/ModelSubstitutionTest/enum_substitution/custom-openapi-model.yml");
		apiConfiguration.setModelsAssociationsPath(
			"src/test/resources/ut/ModelSubstitutionTest/enum_substitution/custom-model-association.yml");
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Arrays.asList(ResponseEntityController.class.getCanonicalName()));
		apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/ModelSubstitutionTest/enum_substitution/enum_substitution.yml", generated.get(0));
	}

}
