package io.github.kbuntrock.resources.endpoint.generic;


import io.github.kbuntrock.resources.dto.genericity.mappingObject.MapWithObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mapping-object")
public interface MappingObject {

	@GetMapping(path = "/object")
	Object getObject();

	@PostMapping(path = "/object")
	void postObject(@RequestBody Object anything);

	@GetMapping(path = "/object-map")
	MapWithObject getObjectMap();

}
