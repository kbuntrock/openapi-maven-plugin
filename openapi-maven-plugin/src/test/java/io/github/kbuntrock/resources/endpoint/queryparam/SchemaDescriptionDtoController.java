package io.github.kbuntrock.resources.endpoint.queryparam;

import io.github.kbuntrock.resources.dto.SchemaDescriptionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/schema-description")
public interface SchemaDescriptionDtoController {

	@GetMapping("query")
	boolean query(SchemaDescriptionDto filter);

}
