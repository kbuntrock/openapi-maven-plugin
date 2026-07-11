package io.github.kbuntrock.v3_1;

import io.github.kbuntrock.AbstractTest;
import io.github.kbuntrock.DocumentationMojo;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.javadoc.basic.BasicController;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class OpenApi3_1 extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("My Project");
		mavenProjet.setVersion("10.5.36");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	private DocumentationMojo createBasicMojo(final String apiLocation) {
		final DocumentationMojo mojo = createDocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Collections.singletonList(apiLocation));
		apiConfiguration.setOpenapiVersion("3.1.0");
		apiConfiguration
			.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());
		return mojo;
	}

	@Test
	public void nominal_3_1_generation() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(BasicController.class.getCanonicalName());

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/basic",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}
}
