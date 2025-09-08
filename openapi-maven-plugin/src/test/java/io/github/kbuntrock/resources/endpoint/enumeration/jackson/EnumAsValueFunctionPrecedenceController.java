package io.github.kbuntrock.resources.endpoint.enumeration.jackson;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.enumeration.EnumAsValueFunctionPrecedence;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/my-controller")
public interface EnumAsValueFunctionPrecedenceController {

	@GetMapping("/enums")
	EnumAsValueFunctionPrecedence[] getEnums();
}
