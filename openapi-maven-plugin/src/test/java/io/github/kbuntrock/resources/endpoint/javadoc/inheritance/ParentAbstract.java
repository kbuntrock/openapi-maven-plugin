package io.github.kbuntrock.resources.endpoint.javadoc.inheritance;

/**
 * @author Kevin Buntrock
 */
public abstract class ParentAbstract extends GrandParentAbstract implements GrandParentInterface {

	/**
	 * Encapsulate a number in a beautiful string representation
	 *
	 * @param number
	 * @return encapsulate a number in a beautiful string representation
	 */
	public String encapsulate(final long number) {
		return "__*" + number + "*__";
	}
}
