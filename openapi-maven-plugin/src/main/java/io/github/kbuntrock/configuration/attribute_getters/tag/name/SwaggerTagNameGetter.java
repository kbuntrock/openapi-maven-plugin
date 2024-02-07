package io.github.kbuntrock.configuration.attribute_getters.tag.name;

import java.util.Optional;

public class SwaggerTagNameGetter extends AbstractTagNameGetter {

    private final Class<?> clazz;

    public SwaggerTagNameGetter(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Optional<String> getTagName() {
        if (clazz.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            return Optional.of(clazz.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).name());
        }
        return Optional.empty();
    }

}
