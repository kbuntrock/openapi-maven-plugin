package io.github.kbuntrock.resources.endpoint.queryparam;

import io.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping("api")
public interface QueryParamDtoBindingController {

	@GetMapping("is-time-valid")
	boolean isTimeValid(TimeDto time);

}
