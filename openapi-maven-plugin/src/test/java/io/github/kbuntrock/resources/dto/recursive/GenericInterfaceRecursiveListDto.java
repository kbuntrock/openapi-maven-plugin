package io.github.kbuntrock.resources.dto.recursive;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public interface GenericInterfaceRecursiveListDto<E> {

	String getName();

	E getWrapped();

	List<GenericInterfaceRecursiveListDto<E>> getChildList();
}
