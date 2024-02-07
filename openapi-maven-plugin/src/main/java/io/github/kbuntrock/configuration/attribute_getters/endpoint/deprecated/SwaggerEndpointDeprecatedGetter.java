package io.github.kbuntrock.configuration.attribute_getters.endpoint.deprecated;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.reflect.Method;
import java.util.Optional;

public class SwaggerEndpointDeprecatedGetter extends AbstractEndpointDeprecatedGetter {

    private final Method method;

    public SwaggerEndpointDeprecatedGetter(Method method) {
        this.method = method;
    }

    public Optional<Boolean> getEndpointDeprecated() {
        if (method.isAnnotationPresent(Operation.class)) {
            return Optional.of(method.getAnnotation(Operation.class).deprecated());
        }
        return Optional.empty();
    }

}
