package io.github.kbuntrock.configuration.attribute_getters.endpoint.deprecated;

import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.util.Optional;

public abstract class AbstractEndpointDeprecatedGetter {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected AbstractEndpointDeprecatedGetter() {
	}

	public abstract Optional<Boolean> getEndpointDeprecated();

}
