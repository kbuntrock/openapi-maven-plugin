package io.github.kbuntrock.resources.dto.recursive;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Kevin Buntrock
 */

public interface GenericRecursiveInterfaceDto<G> {

	@JsonProperty(index = 2)
	String getName();

	@JsonProperty(index = 1)
	GenericRecursiveInterfaceDto<G> getChild();
}
