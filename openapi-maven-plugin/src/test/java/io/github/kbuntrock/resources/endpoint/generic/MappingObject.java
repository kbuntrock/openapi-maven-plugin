package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.mappingObject.ChildMapWithObject;
import io.github.kbuntrock.resources.dto.genericity.mappingObject.MapWithObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mapping-object")
public interface MappingObject {

	// ChildMapWithObject
	//return Object
	@GetMapping(path = "/foo")
	ChildMapWithObject getMyMap();

}
