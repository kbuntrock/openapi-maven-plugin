package io.github.kbuntrock.configuration.attribute_getters.endpoint.description;

import io.github.kbuntrock.javadoc.JavadocWrapper;

import java.lang.reflect.Method;
import java.util.Optional;

public class JavadocEndpointDescriptionGetter extends AbstractEndpointDescriptionGetter {
    private final JavadocWrapper methodJavadoc;

    public JavadocEndpointDescriptionGetter(JavadocWrapper methodJavadoc) {
        this.methodJavadoc = methodJavadoc;
    }

    public Optional<String> getEndpointDescription(final Method method) {
        if (methodJavadoc == null) {
            return Optional.empty();
        }
        return Optional.of(methodJavadoc.getJavadoc().getDescription().toText());
    }

}
