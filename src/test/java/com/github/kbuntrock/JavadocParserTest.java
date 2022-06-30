package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.javadoc.JavadocParser;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.resources.endpoint.file.FileUploadController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

public class JavadocParserTest extends AbstractTest {

    @BeforeAll
    public static void initTest() {
        ReflectionsUtils.initiateTestMode();
    }


    private MavenProject createBasicMavenProject() {
        MavenProject mavenProjet = new MavenProject();
        mavenProjet.setName("My Project");
        mavenProjet.setVersion("10.5.36");
        return mavenProjet;
    }

    private File createTestFile() throws IOException {
        return Files.createTempFile("openapi_test_", ".yml").toFile();
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

        JavadocParser javadocParser = new JavadocParser(Arrays.asList(new File("src/test/java/com/github/kbuntrock/resources")));
        javadocParser.scan();

        SpringClassAnalyser analyser = new SpringClassAnalyser(apiConfiguration);
        Optional<Tag> tag = analyser.getTagFromClass(FileUploadController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        File generatedFile = new File("target/toto.yml");

        new YamlWriter(createBasicMavenProject(), apiConfiguration, javadocParser.getJavadocMap()).write(generatedFile, library);

    }


}
