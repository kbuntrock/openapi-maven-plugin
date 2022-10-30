package io.github.kbuntrock.resources.dto.recursive;

/**
 * @author Kevin Buntrock
 */
public interface GenericRecursiveInterfaceDto<G> {

	String getName();

	GenericRecursiveInterfaceDto<G> getChild();
}
