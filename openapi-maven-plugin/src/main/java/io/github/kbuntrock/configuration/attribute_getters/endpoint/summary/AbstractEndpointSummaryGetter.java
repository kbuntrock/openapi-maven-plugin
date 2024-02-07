package io.github.kbuntrock.configuration.attribute_getters.endpoint.summary;

import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.lang.reflect.Method;
import java.util.Optional;

public abstract class AbstractEndpointSummaryGetter {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected AbstractEndpointSummaryGetter() {
	}

	public abstract Optional<String> getEndpointSummary(final Method clazz);

}
