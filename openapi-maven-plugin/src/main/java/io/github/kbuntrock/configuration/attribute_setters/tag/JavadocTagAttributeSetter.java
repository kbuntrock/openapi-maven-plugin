package io.github.kbuntrock.configuration.attribute_setters.tag;

import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

public class JavadocTagAttributeSetter extends AbstractTagAttributeSetter {
    private final Log logger = Logger.INSTANCE.getLogger();

    private final ClassDocumentation classDocumentation;

    public JavadocTagAttributeSetter(ClassDocumentation classDocumentation) {
        this.classDocumentation = classDocumentation;
    }

    @Override
    public void process(final Tag tag) {
        if (classDocumentation == null) {
            return;
        }
        // Even if there is no declared class documentation, we may enhance it with javadoc on interface and/or abstract classes
        logger.debug(
                "Class documentation found for tag " + tag.getClazz().getSimpleName() + " ? " + (classDocumentation != null));

        classDocumentation.inheritanceEnhancement(tag.getClazz(), ClassDocumentation.EnhancementType.METHODS);
        if (classDocumentation.getDescription().isPresent()) {
            tag.setDescription(classDocumentation.getDescription().get());
        }
    }


}
