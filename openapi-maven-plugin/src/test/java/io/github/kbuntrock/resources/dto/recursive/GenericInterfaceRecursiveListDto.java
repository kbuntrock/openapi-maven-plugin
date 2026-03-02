package io.github.kbuntrock.resources.dto.recursive;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public interface GenericInterfaceRecursiveListDto<E> {

	@JsonProperty(index = 2)
	void setName(String s);

	@JsonProperty(index = 3)
	void setWrapped(E dto);

	@JsonProperty(index = 1)
	void setChildList(List<GenericInterfaceRecursiveListDto<E>> something);
}
