package io.github.kbuntrock.resources.dto.recursive;

/**
 * A recursive and generic object
 *
 * @author Kevin Buntrock
 */
public class GenericRecursiveDto<G> {

	/**
	 * A non recursive property
	 */
	private String name;

	/**
	 * The generic wrapped object
	 */
	private G wrapped;

	/**
	 * The recursive property
	 */
	private GenericRecursiveDto<G> child;

	public String getName() {
		return name;
	}

	public G getWrapped() {
		return wrapped;
	}

	public GenericRecursiveDto<G> getChild() {
		return child;
	}
}
