package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumAsValueFunctionPrecedenceController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumFieldAsValueController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumFunctionAsValueController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumTooMuchAsValueController;
import io.github.kbuntrock.resources.endpoint.jackson.inputoutput.JacksonController1;
import io.github.kbuntrock.resources.endpoint.jackson.inputoutput.JacksonController2;
import io.github.kbuntrock.resources.endpoint.jackson.inputoutput.JacksonController3;
import io.github.kbuntrock.resources.endpoint.jackson.inputoutput.JacksonController4;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class JacksonAnalyserTest extends AbstractTest {

	private MavenProject createBasicMavenProject() {
		final MavenProject mavenProjet = new MavenProject();
		mavenProjet.setName("Project using jackson");
		mavenProjet.setVersion("v1.0");
		mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
		return mavenProjet;
	}

	private DocumentationMojo createBasicMojo(final String... apiLocation) {
		final DocumentationMojo mojo = createDocumentationMojo();
		final ApiConfiguration apiConfiguration = new ApiConfiguration();
		apiConfiguration.setAttachArtifact(false);
		apiConfiguration.setLocations(Arrays.asList(apiLocation));
		apiConfiguration
			.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
		mojo.setTestMode(true);
		mojo.setApis(Collections.singletonList(apiConfiguration));
		mojo.setProject(createBasicMavenProject());
		return mojo;
	}

	@Test
	public void enum_field_as_value() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enum_function_as_value() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFunctionAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enum_as_value_function_precedence() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumAsValueFunctionPrecedenceController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void enum_too_much_as_value() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumTooMuchAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		checkGenerationResult(mojo.documentProject());
		Mockito.verify(mojo.getContext().getLogger()).warn(
			"Problem with definition of [io.github.kbuntrock.resources.dto.enumeration.EnumTooMuchAsValue]: Multiple 'as-value' methods defined [getCode,getNormalizedCode]");
	}

	@Test
	public void disable_enum_name_extension() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		mojo.getApis().get(0).setEnumNameExtensionEnabled(false);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void configure_enum_name_extension() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		// Change the "value name" holder
		mojo.getApis().get(0).setEnumNameExtensionValue("x-enumNames");

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void disable_enum_description_extension() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		mojo.getApis().get(0).setEnumDescriptionExtensionEnabled(false);

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void configure_enum_description_extension() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		// Change the "value description" holder
		mojo.getApis().get(0).setEnumDescriptionExtensionValue("x-my-description");

		checkGenerationResult(mojo.documentProject());
	}

	@Test
	public void disable_enum_list_description() throws MojoFailureException, MojoExecutionException, IOException {
		final DocumentationMojo mojo = createBasicMojo(EnumFieldAsValueController.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/enumeration/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/enumeration"));
		mojo.setJavadocConfiguration(javadocConfig);

		mojo.getApis().get(0).setEnumListDescriptionEnabled(false);

		checkGenerationResult(mojo.documentProject());
	}

	/**
	 * OrderJacksonDto and AccountJacksonDto in read only
	 */
	@Test
	public void schema_only_for_output() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(JacksonController1.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/jackson"));
		mojo.setJavadocConfiguration(javadocConfig);
		checkGenerationResult(mojo.documentProject());
	}

	/**
	 * OrderJacksonDto and AccountJacksonDto in write only
	 */
	@Test
	public void schema_only_for_input() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(JacksonController2.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/jackson"));
		mojo.setJavadocConfiguration(javadocConfig);
		checkGenerationResult(mojo.documentProject());
	}

	/**
	 * OrderJacksonDto in read only, and AccountJacksonDto in read/write
	 */
	@Test
	public void schema_only_for_input_and_output_1() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(JacksonController1.class.getCanonicalName(),
			JacksonController3.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/jackson"));
		mojo.setJavadocConfiguration(javadocConfig);
		checkGenerationResult(mojo.documentProject());
	}

	/**
	 * OrderJacksonDto and AccountJacksonDto both in read/write only
	 */
	@Test
	public void schema_only_for_input_and_output_2() throws MojoExecutionException, MojoFailureException, IOException {
		final DocumentationMojo mojo = createBasicMojo(JacksonController1.class.getCanonicalName(),
			JacksonController4.class.getCanonicalName());
		final JavadocConfiguration javadocConfig = new JavadocConfiguration();
		javadocConfig.setScanLocations(Arrays.asList("src/test/java/io/github/kbuntrock/resources/endpoint/jackson",
			"src/test/java/io/github/kbuntrock/resources/dto/jackson"));
		mojo.setJavadocConfiguration(javadocConfig);
		checkGenerationResult(mojo.documentProject());
	}
}
