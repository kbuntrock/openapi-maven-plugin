package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration1Controller;
import io.github.kbuntrock.resources.endpoint.javadoc.inheritance.ChildClassOne;
import io.github.kbuntrock.resources.endpoint.javadoc.inheritance.two.ChildClassTwo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavadocParserTest extends AbstractTest {

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
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Collections.singletonList(apiLocation));
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
	public void inheritance_test_one() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(ChildClassOne.class.getCanonicalName());

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/inheritance",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/JavadocParserTest/inheritance_test_one.yml", generated.get(0));
	}

	@Test
	public void inheritance_test_two() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(ChildClassTwo.class.getCanonicalName());
		mojo.getApis().get(0).setTagAnnotations(Arrays.asList(TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName(),
			TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/inheritance",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/JavadocParserTest/inheritance_test_two.yml", generated.get(0));
	}

	@Test
	public void inheritance_test_two_package_error() throws MojoFailureException, MojoExecutionException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint.javadoc.inheritance.two");
		mojo.getApis().get(0).setTagAnnotations(Arrays.asList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName(),
			TagAnnotation.SPRING_REST_CONTROLLER.getAnnotationClassName()));

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/inheritance",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		MojoRuntimeException exception = null;
		try {
			mojo.documentProject();
		} catch(final MojoRuntimeException ex) {
			exception = ex;
		}
		Assertions.assertNotNull(exception);
		Assertions.assertEquals("More than one operation mapped on GET : /api/child-class-two/age-plus-one in tag IChildClassTwo",
			exception.getMessage());
	}

	@Test
	public void inheritance_test_two_package_success() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint.javadoc.inheritance.two");
		final List<String> tags = new ArrayList<>();
		tags.add("RestController");
		mojo.getApis().get(0).setTagAnnotations(tags);

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/inheritance",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/JavadocParserTest/inheritance_test_two.yml", generated.get(0));
	}

	@Test
	public void inheritance_test_three() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo("io.github.kbuntrock.resources.endpoint.javadoc.inheritance.three");
		final List<String> tags = new ArrayList<>();
		tags.add("RestController");
		mojo.getApis().get(0).setTagAnnotations(tags);

		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/javadoc/inheritance",
			"src/test/java/io/github/kbuntrock/resources/dto"));
		mojo.setJavadocConfiguration(javadocConfig);

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/JavadocParserTest/inheritance_test_three.yml", generated.get(0));
	}

	@Test
	public void default_error_responses() throws MojoFailureException, MojoExecutionException, IOException {

		final DocumentationMojo mojo = createBasicMojo(TestEnumeration1Controller.class.getCanonicalName());
		final ApiConfiguration apiConfiguration = mojo.getApis().get(0);

		final InputStream freeFieldsFileStream = this.getClass().getClassLoader()
			.getResourceAsStream("ut/JavadocParserTest/freeFields/default_error_responses_free_fields.txt");
		apiConfiguration.setFreeFields(IOUtils.toString(freeFieldsFileStream, StandardCharsets.UTF_8));

		final InputStream defaultErrorsFileStream = this.getClass().getClassLoader()
			.getResourceAsStream("ut/JavadocParserTest/freeFields/default_error_responses.txt");
		apiConfiguration.setDefaultErrors(IOUtils.toString(defaultErrorsFileStream, StandardCharsets.UTF_8));

		apiConfiguration.setPathPrefix("/v1");

		final List<File> generated = mojo.documentProject();
		checkGenerationResult("ut/SpringClassAnalyserTest/enumeration_test_1_with_default_errors.yml", generated.get(0));

	}

}
