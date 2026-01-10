package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.mappingObject.MapWithObject;
import java.util.Map;
import org.springframework.http.MediaType;
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

	@GetMapping(path = "/non-wrapped-object-map", produces = MediaType.APPLICATION_JSON_VALUE)
	Map<String, Object> getNonWrappedObjectMap();
}
