package com.github.kbuntrock;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.resources.endpoint.AccountController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class SpringClassAnalyserTest extends AbstractTest {

    @Test
    public void basicParsing() throws MojoFailureException, IOException {

        MavenProject mavenProject = new MavenProject();
        mavenProject.setName("Mon super projet");
        mavenProject.setVersion("2.5.9-SNAPSHOT");
        ClassLoader projectClassLoader = AccountController.class.getClassLoader();
        SpringClassAnalyser analyser = new SpringClassAnalyser(projectClassLoader);
        Optional<Tag> tag = analyser.getTagFromClass(AccountController.class);
        TagLibrary library = new TagLibrary(projectClassLoader);
        library.addTag(tag.get());

        ApiConfiguration apiConfiguration = new ApiConfiguration();
        new YamlWriter(projectClassLoader, mavenProject, apiConfiguration).write(new File("D:\\Dvpt\\openapi-maven-plugin\\target\\component.yml"), library);

        System.out.println();
    }


    @Test
    public void test() throws NoSuchFieldException {

        Field field = MaClass.class.getField("monChamp");
        Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        System.out.println("");

    }

    public class MaClass {
        public List<String> monChamp;
    }
}
