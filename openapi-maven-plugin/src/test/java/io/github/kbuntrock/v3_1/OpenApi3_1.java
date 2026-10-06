package io.github.kbuntrock.v3_1;

import io.github.kbuntrock.AbstractTest;
import io.github.kbuntrock.DocumentationMojo;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration1Controller;
import io.github.kbuntrock.resources.endpoint.javadoc.basic.BasicController;
import io.github.kbuntrock.resources.endpoint.swagger.EntityAnnotationResource;
import io.github.kbuntrock.resources.endpoint.swagger.EntityAnnotationWithParametersResource;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
	public void basicAnnotatedAndJavadocResponseWithReturnObjects()
		throws MojoFailureException, IOException, MojoExecutionException {
		final DocumentationMojo mojo = createBasicMojo(EntityAnnotationResource.class.getCanonicalName());
		JavadocConfiguration javadocConfiguration = new JavadocConfiguration();
		javadocConfiguration
			.setScanLocations(
				Collections.singletonList("src/test/java/io/github/kbuntrock/resources/endpoint/swagger"));
		mojo.setJavadocConfiguration(javadocConfiguration);
		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void new_free_fields() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(EntityAnnotationResource.class.getCanonicalName());
		final ApiConfiguration apiConfiguration = mojo.getApis().get(0);

		final InputStream freeFieldsFileStream = this.getClass().getClassLoader()
			.getResourceAsStream("ut/OpenAPI3_1/3_1_free_fields.txt");
		apiConfiguration.setFreeFields(IOUtils.toString(freeFieldsFileStream, StandardCharsets.UTF_8));

		checkGenerationResult(mojo.documentProject());

	}
}
