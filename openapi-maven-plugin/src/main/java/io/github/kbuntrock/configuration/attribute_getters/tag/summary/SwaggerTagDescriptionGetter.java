package io.github.kbuntrock.configuration.attribute_getters.tag.summary;

import java.util.Optional;

public class SwaggerTagDescriptionGetter extends AbstractTagDescriptionGetter {

    public Optional<String> getTagDescription(final Class<?> clazz) {
        if (clazz.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            return Optional.of(clazz.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).description());
        }
        return Optional.empty();
    }

}
