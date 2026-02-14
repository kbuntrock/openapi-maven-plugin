package io.github.kbuntrock.resources.dto.recursive;

/**
 * @author Kevin Buntrock
 */
public class RecursiveDto {

	private String name;
	private RecursiveDto child;

	public String getName() {
		return name;
	}

	public RecursiveDto getChild() {
		return child;
	}
}
