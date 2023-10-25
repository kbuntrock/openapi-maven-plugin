package io.github.kbuntrock.resources.dto.genericity.issue89;

/**
 * The IPair object
 */
public interface IPair<A, B> extends ISingle<A> {

	/**
	 * Get the B attribute
	 *
	 * @return the B attribute
	 */
	B getB();
}
