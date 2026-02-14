package io.github.kbuntrock.resources.dto.genericity.issue144;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The child request item interface
 */
@JsonPropertyOrder({ "baseField", "childField" })
public interface ChildRequestItemInterface extends BaseRequestItemInterface {

	/**
	 * The child field
	 */
	String getChildField();

}
