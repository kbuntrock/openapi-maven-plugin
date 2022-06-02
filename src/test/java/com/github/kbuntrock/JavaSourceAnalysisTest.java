package com.github.kbuntrock;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.resources.dto.AccountDto;
import com.github.kbuntrock.resources.dto.Authority;
import com.github.kbuntrock.resources.dto.SpeAccountDto;
import com.github.kbuntrock.resources.dto.TimeDto;
import com.github.kbuntrock.resources.endpoint.javasource.ControllerOne;
import com.github.kbuntrock.resources.endpoint.javasource.ControllerThree;
import com.github.kbuntrock.resources.endpoint.javasource.ControllerTwo;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

/**
 * Check if all DataObjects which will endup in the schema reference section are correctly marked.
 *
 * @author Kevin Buntrock
 */
public class JavaSourceAnalysisTest extends AbstractTest {

    private boolean containsClassRepresentation(Set<DataObject> set, Class<?> clazz) {
        for (DataObject dataObject : set) {
            if (dataObject.getJavaClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void controllerOne() throws MojoFailureException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(ControllerOne.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        Assertions.assertEquals(3, library.getSchemaObjects().size());
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
    }

    @Test
    public void controllerTwo() throws MojoFailureException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(ControllerTwo.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        Assertions.assertEquals(3, library.getSchemaObjects().size());
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
    }

    @Test
    public void controllerThree() throws MojoFailureException {

        SpringClassAnalyser analyser = new SpringClassAnalyser();
        Optional<Tag> tag = analyser.getTagFromClass(ControllerThree.class);
        TagLibrary library = new TagLibrary();
        library.addTag(tag.get());

        Assertions.assertEquals(4, library.getSchemaObjects().size());
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), TimeDto.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), Authority.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), SpeAccountDto.class));
        Assertions.assertTrue(containsClassRepresentation(library.getSchemaObjects(), AccountDto.class));
    }


}
