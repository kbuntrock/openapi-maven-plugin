package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.javadoc.JavadocParser;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.resources.endpoint.AccountController;
import com.github.kbuntrock.resources.endpoint.generic.GenericityController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SpringClassAnalyserTest extends AbstractTest {

    @BeforeAll
    public static void initTest() {
        ReflectionsUtils.initiateTestMode();
    }

    @Test
    public void parsingGenerics() throws MojoFailureException, IOException {

        ReflectionsUtils.initiateTestMode();
        List<String> apiLocations = List.of("com.github.kbuntrock.resources.endpoint");
        ClassLoader projectClassLoader = GenericityController.class.getClassLoader();
        SpringResourceParser parser = new SpringResourceParser(apiLocations);
        TagLibrary tagLibrary = parser.scanRestControllers();

        MavenProject mavenProject = new MavenProject();
        mavenProject.setName("Big parsing");
        mavenProject.setVersion("1.0.0");

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        new YamlWriter(mavenProject, apiConfiguration).write(
                new File("D:\\Dvpt\\openapi-maven-plugin\\target\\component.yml"),
                tagLibrary);

    }


    @Test
    public void basicParsing() throws MojoFailureException, IOException {

        MavenProject mavenProject = new MavenProject();
        mavenProject.setName("Mon super projet");
        mavenProject.setVersion("2.5.9-SNAPSHOT");
        ClassLoader projectClassLoader = AccountController.class.getClassLoader();
        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(GenericityController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        new YamlWriter(mavenProject, apiConfiguration).write(new File("D:\\Dvpt\\openapi-maven-plugin\\target\\component.yml"), library);

        System.out.println();
    }

    @Test
    public void javadocParsing() throws IOException {
        JavadocParser parser = new JavadocParser("D:\\Dvpt\\openapi-maven-plugin", true);
        parser.analyseClass(AccountController.class);
    }
}
