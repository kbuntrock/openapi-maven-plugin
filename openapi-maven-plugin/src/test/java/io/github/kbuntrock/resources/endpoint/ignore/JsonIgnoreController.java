package io.github.kbuntrock.resources.endpoint.ignore;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.ignore.JsonIgnoreDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/json-ignore")
public interface JsonIgnoreController {

	@GetMapping("/get")
	JsonIgnoreDto get();
}
