package io.github.kbuntrock.resources.dto.genericity.issue89;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This is the IBar object
 */
@JsonPropertyOrder({ "a", "b", "bar", "boo", "foo" })
public interface IBar extends IBoo, IX {

	/**
	 * The bar string
	 *
	 * @return the bar string
	 */
	String getBar();
}
