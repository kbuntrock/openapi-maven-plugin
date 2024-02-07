package io.github.kbuntrock.configuration.attribute_getters.endpoint.description;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.reflect.Method;
import java.util.Optional;

public class SwaggerEndpointDescriptionGetter extends AbstractEndpointDescriptionGetter {

    public Optional<String> getEndpointDescription(final Method method) {
        if (method.isAnnotationPresent(Operation.class)) {
            final String description = method.getAnnotation(Operation.class).description();
            return !description.isEmpty() ? Optional.of(description) : Optional.empty();
        }
        return Optional.empty();
    }

}
