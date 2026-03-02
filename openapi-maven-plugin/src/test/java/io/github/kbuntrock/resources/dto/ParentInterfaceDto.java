package io.github.kbuntrock.resources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a parent interface
 *
 * @author Kevin Buntrock
 */
public interface ParentInterfaceDto {

	/**
	 * Get the parent id
	 *
	 * @return the parent id
	 */
	@JsonProperty(index = 4)
	int getParentId();
}
