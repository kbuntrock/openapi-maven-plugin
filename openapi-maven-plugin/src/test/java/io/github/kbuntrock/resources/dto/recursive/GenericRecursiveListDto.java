package io.github.kbuntrock.resources.dto.recursive;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class GenericRecursiveListDto<G> {

	private String name;
	private G wrapped;
	private List<GenericRecursiveListDto<G>> childList;

	public String getName() {
		return name;
	}

	public G getWrapped() {
		return wrapped;
	}

	public List<GenericRecursiveListDto<G>> getChildList() {
		return childList;
	}
}
