package io.github.kbuntrock.resources.dto.recursive;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Kevin Buntrock
 */
public interface GenericInterfaceRecursiveListDto<E> {

	@JsonProperty(index = 2)
	String getName();

	@JsonProperty(index = 3)
	E getWrapped();

	@JsonProperty(index = 1)
	List<GenericInterfaceRecursiveListDto<E>> getChildList();
}
