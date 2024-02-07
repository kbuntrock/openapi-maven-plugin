package io.github.kbuntrock.configuration.attribute_getters.tag.summary;

import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocMap;

import java.util.Optional;

public class JavadocTagDescriptionGetter extends AbstractTagDescriptionGetter {

    public Optional<String> getTagDescription(final Class<?> clazz) {
        if (JavadocMap.INSTANCE.isPresent()) {
            ClassDocumentation classDocumentation = JavadocMap.INSTANCE
                    .getJavadocMap()
                    .computeIfAbsent(clazz.getCanonicalName(), k -> new ClassDocumentation(clazz.getCanonicalName(), clazz.getSimpleName()));
            // Even if there is no declared class documentation, we may enhance it with javadoc on interface and/or abstract classes
            logger.debug(
                    "Class documentation found for tag " + clazz.getSimpleName() + " ? " + (classDocumentation != null));

            classDocumentation.inheritanceEnhancement(clazz, ClassDocumentation.EnhancementType.METHODS);
            return classDocumentation.getDescription();
        }
        return Optional.empty();
    }

}
