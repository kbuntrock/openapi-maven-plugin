package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.configuration.JavadocConfiguration;
import com.github.kbuntrock.configuration.OperationIdHelper;
import com.github.kbuntrock.javadoc.JavadocMap;
import com.github.kbuntrock.javadoc.JavadocParser;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.resources.endpoint.enumeration.TestEnumeration3Controller;
import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.ChildClassOne;
import com.github.kbuntrock.resources.endpoint.javadoc.inheritance.two.ChildClassTwo;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JavadocParserTest extends AbstractTest {

    private MavenProject createBasicMavenProject() {
        MavenProject mavenProjet = new MavenProject();
        mavenProjet.setName("My Project");
        mavenProjet.setVersion("10.5.36");
        mavenProjet.setFile(new File(new File("pom.xml").getAbsolutePath()));
        return mavenProjet;
    }

    private DocumentationMojo createBasicMojo(String apiLocation) {
        DocumentationMojo mojo = new DocumentationMojo();
        ApiConfiguration apiConfiguration = new ApiConfiguration();
        apiConfiguration.setAttachArtifact(false);
        apiConfiguration.setLocations(Arrays.asList(apiLocation));
        apiConfiguration.setDefaultProduceConsumeGuessing(false);
        apiConfiguration.setOperationId("{method_name}");
        mojo.setTestMode(true);
        mojo.setApis(Arrays.asList(apiConfiguration));
        mojo.setProject(createBasicMavenProject());
        return mojo;
    }

    @Test
    public void inheritance_test_one() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo(ChildClassOne.class.getCanonicalName());

        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/com/github/kbuntrock/resources/endpoint/javadoc/inheritance",
                "src/test/java/com/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/JavadocParserTest/inheritance_test_one.yml", generated.get(0));
    }

    @Test
    public void inheritance_test_two() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo(ChildClassTwo.class.getCanonicalName());

        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/com/github/kbuntrock/resources/endpoint/javadoc/inheritance",
                "src/test/java/com/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/JavadocParserTest/inheritance_test_two.yml", generated.get(0));
    }

    @Test
    public void inheritance_test_two_package_error() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo("com.github.kbuntrock.resources.endpoint.javadoc.inheritance.two");

        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/com/github/kbuntrock/resources/endpoint/javadoc/inheritance",
                "src/test/java/com/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        MojoRuntimeException exception = null;
        try {
            mojo.documentProject();
        } catch (MojoRuntimeException ex) {
            exception = ex;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("More than one operation mapped on GET : /api/child-class-two/age-plus-one in tag ChildClassTwo",
                exception.getMessage());
    }

    @Test
    public void inheritance_test_two_package_success() throws MojoFailureException, MojoExecutionException, IOException {

        DocumentationMojo mojo = createBasicMojo("com.github.kbuntrock.resources.endpoint.javadoc.inheritance.two");
        mojo.getApis().get(0).setTagAnnotations(Arrays.asList("RestController"));

        JavadocConfiguration javadocConfig = new JavadocConfiguration();
        javadocConfig.setScanLocations(Arrays.asList("src/test/java/com/github/kbuntrock/resources/endpoint/javadoc/inheritance",
                "src/test/java/com/github/kbuntrock/resources/dto"));
        mojo.setJavadocConfiguration(javadocConfig);

        List<File> generated = mojo.documentProject();
        checkGenerationResult("ut/JavadocParserTest/inheritance_test_two.yml", generated.get(0));
    }

    @Test
    public void javadoc_nominal_case() throws IOException {

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        JavadocParser javadocParser = new JavadocParser(Arrays.asList(new File("src/test/java/com/github/kbuntrock/resources/endpoint/file")));
        javadocParser.scan();

    }

    @Test
    public void javadoc_file_upload() throws MojoFailureException, IOException {

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        apiConfiguration.setOperationId("{method_name}");
        apiConfiguration.setOperationIdHelper(new OperationIdHelper(apiConfiguration.getOperationId()));

        JavadocParser javadocParser = new JavadocParser(Arrays.asList(new File("src/test/java/com/github/kbuntrock/resources")));
        javadocParser.scan();
        JavadocMap.INSTANCE.setJavadocMap(javadocParser.getJavadocMap());

        SpringClassAnalyser analyser = new SpringClassAnalyser(apiConfiguration);
        Optional<Tag> tag = analyser.getTagFromClass(TestEnumeration3Controller.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        File generatedFile = new File("target/toto.yml");

        new YamlWriter(createBasicMavenProject(), apiConfiguration).write(generatedFile, library);

    }


}
