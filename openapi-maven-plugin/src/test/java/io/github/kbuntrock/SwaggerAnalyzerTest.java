package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.swagger.ApiResponseResource;
import io.github.kbuntrock.resources.endpoint.swagger.EntityAnnotationResource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class SwaggerAnalyzerTest extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("My Project");
		mavenProjet.setVersion("10.5.36");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	private DocumentationMojo createBasicMojo(final String... apiLocation) {
		final DocumentationMojo mojo = new DocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Arrays.asList(apiLocation));
		apiConfiguration.setDefaultProduceConsumeGuessing(false);
		apiConfiguration.setOperationId("{method_name}");
		apiConfiguration.setLoopbackOperationName(false);
		apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());
		return mojo;
	}


	@Test
	public void basicApiResponseWithReturnObjects() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(ApiResponseResource.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());

	}

	@Test
	public void basicAnnotatedResponseWithReturnObjects() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(EntityAnnotationResource.class.getCanonicalName());

		checkGenerationResult(mojo.documentProject());

	}

	@Test
	public void basicAnnotatedAndJavadocResponseWithReturnObjects() throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(EntityAnnotationResource.class.getCanonicalName());
		JavadocConfiguration javadocConfiguration = new JavadocConfiguration();
		javadocConfiguration.setScanLocations(Collections.singletonList("src/test/java/io/github/kbuntrock/resources/endpoint/swagger"));
		mojo.setJavadocConfiguration(javadocConfiguration);
		checkGenerationResult(mojo.documentProject());
	}
}
