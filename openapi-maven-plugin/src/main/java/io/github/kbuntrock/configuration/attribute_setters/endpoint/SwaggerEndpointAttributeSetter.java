package io.github.kbuntrock.configuration.attribute_setters.endpoint;

import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ExternalDocs;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;

public class SwaggerEndpointAttributeSetter extends AbstractEndpointAttributeSetter {

    @Override
    public void process(final Endpoint endpoint) {
        parseOperationAnnotation(endpoint);
    }

    private void parseOperationAnnotation(final Endpoint endpoint) {
        if (endpoint.getMethod().isAnnotationPresent(Operation.class)) {
            final Operation operation = endpoint.getMethod().getAnnotation(Operation.class);
            // OperationId
            if (!operation.operationId().isEmpty()) {
                endpoint.setOperationId(operation.operationId());
            }
            // Deprecated
            endpoint.setDeprecated(operation.deprecated());
            // Summary
            final String summary = operation.summary();
            if (!summary.isEmpty()) {
                endpoint.setSummary(summary);
            }
            // Description
            final String description = operation.description();
            if (!description.isEmpty()) {
                endpoint.setDescription(description);
            }
            // External docs
            ExternalDocumentation externalDocumentation = operation.externalDocs();
            if (!externalDocumentation.url().isEmpty() || !externalDocumentation.description().isEmpty()) {
                endpoint.setExternalDocs(new ExternalDocs(
                        externalDocumentation.url(),
                        externalDocumentation.description()
                ));
            }
        }
    }
}
