package io.github.kbuntrock.resources.dto.recursive;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public interface GenericInterfaceRecursiveListDto<G> {

    String getName();

    G getWrapped();

    List<GenericInterfaceRecursiveListDto<G>> getChildList();
}
