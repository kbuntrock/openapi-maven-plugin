package com.github.kbuntrock;

import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.resources.endpoint.AccountController;
import com.github.kbuntrock.yaml.YamlWriter;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class SpringClassAnalyserTest {

    @Test
    public void basicParsing() throws MojoFailureException, IOException {
        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(AccountController.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());


        YamlWriter.INSTANCE.write(new File("D:\\Dvpt\\openapi-maven-plugin\\target\\component.yaml"), library);

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
