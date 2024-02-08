package io.github.kbuntrock.configuration.attribute_getters.tag.name;

import io.github.kbuntrock.model.Tag;

import java.util.Optional;

public class SwaggerTagNameGetter extends AbstractTagNameGetter {

    public Optional<String> getTagName(final Tag tag) {
        if (tag.getClazz().isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            return Optional.of(tag.getClazz().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).name());
        }
        return Optional.empty();
    }

}
