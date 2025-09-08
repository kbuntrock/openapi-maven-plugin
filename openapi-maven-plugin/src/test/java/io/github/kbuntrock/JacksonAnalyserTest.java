package io.github.kbuntrock;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JavadocConfiguration;
import io.github.kbuntrock.configuration.library.TagAnnotation;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumAsValueFunctionPrecedenceController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumFieldAsValueController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumFunctionAsValueController;
import io.github.kbuntrock.resources.endpoint.enumeration.jackson.EnumTooMuchAsValueController;
import io.github.kbuntrock.utils.Logger;
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

    private DocumentationMojo createBasicMojo(final String apiLocation) {
        final DocumentationMojo mojo = new DocumentationMojo();
        final ApiConfiguration apiConfiguration = new ApiConfiguration();
        apiConfiguration.setAttachArtifact(false);
        apiConfiguration.setLocations(Collections.singletonList(apiLocation));
        apiConfiguration.setTagAnnotations(Collections.singletonList(TagAnnotation.SPRING_MVC_REQUEST_MAPPING.getAnnotationClassName()));
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
        Mockito.verify(Logger.INSTANCE.getLogger()).warn("Problem with definition of [io.github.kbuntrock.resources.dto.enumeration.EnumTooMuchAsValue]: Multiple 'as-value' methods defined [getCode,getNormalizedCode]");
    }
}
