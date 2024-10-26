package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.mappingObject.MapWithObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mapping-object")
public interface MappingObject {

	//return Object
	@GetMapping(path = "/foo")
	MapWithObject getMyMap();

}
