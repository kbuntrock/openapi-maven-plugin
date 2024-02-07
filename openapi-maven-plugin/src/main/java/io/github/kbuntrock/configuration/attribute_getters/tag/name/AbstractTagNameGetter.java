package io.github.kbuntrock.configuration.attribute_getters.tag.name;

import io.github.kbuntrock.utils.Logger;
import org.apache.maven.plugin.logging.Log;
import java.util.Optional;

public abstract class AbstractTagNameGetter {

	protected final Log logger = Logger.INSTANCE.getLogger();

	protected AbstractTagNameGetter() {
	}

	public abstract Optional<String> getTagName();

}
