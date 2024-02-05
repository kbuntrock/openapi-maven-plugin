package io.github.kbuntrock.resources.endpoint.optional.object;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.optional.object.OptionalClassDto;
import io.github.kbuntrock.resources.dto.optional.object.OptionalInterfaceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping(Constants.BASE_API + "/optional-controller")
public interface OptionalController {

	@GetMapping("class-value")
	OptionalClassDto getClassValue();

	@GetMapping("interface-value")
	OptionalInterfaceDto getInterfaceValue();

}