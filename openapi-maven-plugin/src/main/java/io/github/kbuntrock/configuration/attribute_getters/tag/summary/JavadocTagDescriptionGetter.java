package io.github.kbuntrock.configuration.attribute_getters.tag.summary;

import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.model.Tag;

import java.util.Optional;

public class JavadocTagDescriptionGetter extends AbstractTagDescriptionGetter {

    public Optional<String> getTagDescription(final Tag tag) {
        if (JavadocMap.INSTANCE.isPresent()) {
            ClassDocumentation classDocumentation = JavadocMap.INSTANCE
                    .getJavadocMap()
                    .computeIfAbsent(tag.getClazz().getCanonicalName(), k -> new ClassDocumentation(tag.getClazz().getCanonicalName(), tag.getClazz().getSimpleName()));
            // Even if there is no declared class documentation, we may enhance it with javadoc on interface and/or abstract classes
            logger.debug(
                    "Class documentation found for tag " + tag.getClazz().getSimpleName() + " ? " + (classDocumentation != null));

            classDocumentation.inheritanceEnhancement(tag.getClazz(), ClassDocumentation.EnhancementType.METHODS);
            return classDocumentation.getDescription();
        }
        return Optional.empty();
    }

}
