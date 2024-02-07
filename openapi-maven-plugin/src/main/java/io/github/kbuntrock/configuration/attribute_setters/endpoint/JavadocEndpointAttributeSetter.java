package io.github.kbuntrock.configuration.attribute_setters.endpoint;

import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.ParameterLocation;
import org.apache.maven.plugin.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavadocEndpointAttributeSetter extends AbstractEndpointAttributeSetter {
    private final Log logger = Logger.INSTANCE.getLogger();

    private final ClassDocumentation classDocumentation;

    public JavadocEndpointAttributeSetter(ClassDocumentation classDocumentation) {
        this.classDocumentation = classDocumentation;
    }

    @Override
    public void process(final Endpoint endpoint) {
        if (classDocumentation == null) {
            return;
        }
        JavadocWrapper methodJavadoc = classDocumentation.getMethodsJavadoc().get(endpoint.getIdentifier());
        if (methodJavadoc == null) {
            return;
        }
        methodJavadoc.sortTags();
        String description = methodJavadoc.getJavadoc().getDescription().toText();
        if (description != null && !description.isEmpty()) {
            endpoint.setDescription(description);
        }
        // Body
        // There can be only one body
        final List<ParameterObject> bodies = endpoint.getParameters().stream()
                                                     .filter(x -> ParameterLocation.BODY == x.getLocation())
                                                     .collect(Collectors.toList());
        if (bodies.size() > 1) {
            logger.warn("More than one body is not allowed : "
                    + endpoint.getPath() + " - " + endpoint.getType());
        }
        if (!bodies.isEmpty()) {
            final ParameterObject body = bodies.get(0);
            final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(body.getName());
            if (parameterDoc.isPresent()) {
                final String bodyDescription = parameterDoc.get().getContent().toText();
                if (!bodyDescription.isEmpty()) {
                    body.setDescription(parameterDoc.get().getContent().toText());
                }
            }
            logger.debug(
                    "Parameter documentation found for endpoint body " + body.getName() + " ? "
                            + parameterDoc.isPresent());

        }
        // Non body
        for (final ParameterObject parameter : endpoint.getParameters().stream()
                                                       .filter(x -> ParameterLocation.BODY != x.getLocation()).collect(Collectors.toList())) {
            final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(parameter.getName());
            if (parameterDoc.isPresent()) {
                final String parameterDescription = parameterDoc.get().getContent().toText();
                if (!parameterDescription.isEmpty()) {
                    parameter.setDescription(parameterDoc.get().getContent().toText());
                }
            }
            logger.debug(
                    "Parameter documentation found for endpoint parameter " + parameter.getName() + " ? "
                            + parameterDoc.isPresent());
        }
        // Responses
        final Optional<JavadocBlockTag> returnDoc = methodJavadoc.getReturnBlockTag();
        if (returnDoc.isPresent()) {
            final String responseDescription = returnDoc.get().getContent().toText();
            if (!responseDescription.isEmpty()) {
                for (io.github.kbuntrock.model.Response modelResponse : endpoint.getResponses().values()) {
                    modelResponse.setDescription(returnDoc.get().getContent().toText());
                }
            }
            logger.debug(
                    "Return documentation found ? " + returnDoc.isPresent());
        }
    }


}
