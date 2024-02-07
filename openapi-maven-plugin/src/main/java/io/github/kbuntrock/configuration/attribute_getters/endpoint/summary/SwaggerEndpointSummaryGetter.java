package io.github.kbuntrock.configuration.attribute_getters.endpoint.summary;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.reflect.Method;
import java.util.Optional;

public class SwaggerEndpointSummaryGetter extends AbstractEndpointSummaryGetter {

    public Optional<String> getEndpointSummary(final Method method) {
        if (method.isAnnotationPresent(Operation.class)) {
            final String summary = method.getAnnotation(Operation.class).summary();
            return !summary.isEmpty() ? Optional.of(summary) : Optional.empty();
        }
        return Optional.empty();
    }

}
