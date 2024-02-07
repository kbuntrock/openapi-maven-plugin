package io.github.kbuntrock.configuration.attribute_getters.tag.summary;

import io.github.kbuntrock.model.Tag;

import java.util.Optional;

public class SwaggerTagDescriptionGetter extends AbstractTagDescriptionGetter {

    public Optional<String> getTagDescription(final Tag tag) {
        if (tag.getClazz().isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            return Optional.of(tag.getClazz().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).description());
        }
        return Optional.empty();
    }

}
