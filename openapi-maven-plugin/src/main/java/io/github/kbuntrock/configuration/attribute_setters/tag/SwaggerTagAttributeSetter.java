package io.github.kbuntrock.configuration.attribute_setters.tag;

import io.github.kbuntrock.model.Tag;

public class SwaggerTagAttributeSetter extends AbstractTagAttributeSetter {

    @Override
    public void process(final Tag tag) {
        parseOperationAnnotation(tag);
    }

    private void parseOperationAnnotation(final Tag tag) {
        if (tag.getClazz().isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            final String description = tag.getClazz().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).description();
            if (!description.isEmpty()) {
                tag.setDescription(description);
            }
        }
    }
}
