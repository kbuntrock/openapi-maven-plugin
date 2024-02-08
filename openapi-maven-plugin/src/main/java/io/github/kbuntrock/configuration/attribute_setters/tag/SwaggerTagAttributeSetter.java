package io.github.kbuntrock.configuration.attribute_setters.tag;

import io.github.kbuntrock.model.ExternalDocs;
import io.github.kbuntrock.model.Tag;
import io.swagger.v3.oas.annotations.ExternalDocumentation;

public class SwaggerTagAttributeSetter extends AbstractTagAttributeSetter {

    @Override
    public void process(final Tag tag) {
        parseTagAnnotation(tag);
    }

    private void parseTagAnnotation(final Tag tag) {
        if (tag.getClazz().isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            final io.swagger.v3.oas.annotations.tags.Tag tagAnnotation = tag.getClazz().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class);
            // Description
            final String description = tagAnnotation.description();
            if (!description.isEmpty()) {
                tag.setDescription(description);
            }
            // External docs
            ExternalDocumentation externalDocumentation = tagAnnotation.externalDocs();
            if (!externalDocumentation.url().isEmpty() || !externalDocumentation.description().isEmpty()) {
                tag.setExternalDocs(new ExternalDocs(
                        externalDocumentation.url(),
                        externalDocumentation.description()
                ));
            }
        }
    }
}
