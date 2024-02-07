package io.github.kbuntrock.configuration.attribute_getters.tag.summary;

import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;

import java.util.Optional;

public abstract class AbstractTagDescriptionGetter {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected AbstractTagDescriptionGetter() {
	}

	public abstract Optional<String> getTagDescription(final Class<?> clazz);

}
