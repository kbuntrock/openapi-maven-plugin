package io.github.kbuntrock.configuration.attribute_getters.endpoint.description;

import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.lang.reflect.Method;
import java.util.Optional;

public abstract class AbstractEndpointDescriptionGetter {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected AbstractEndpointDescriptionGetter() {
	}

	public abstract Optional<String> getEndpointDescription(final Method clazz);

}
