package io.github.kbuntrock.resources.dto.recursive;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class GenericRecursiveListDto<G> {

    private String name;
    private G wrapped;
    private List<GenericRecursiveListDto<G>> childList;
}
