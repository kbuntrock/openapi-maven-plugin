package io.github.kbuntrock.resources.endpoint.queryparam;

import io.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kévin Buntrock
 */
@RequestMapping("api")
public interface QueryParamFlatMixNestedDtoBindingController {

	@GetMapping("is-time-valid")
	boolean isTimeValid(
			@RequestParam(value = "queryString", required = false) String queryString,
			TimeDto time,
			@RequestParam(value = "page", required = false, defaultValue = "0") final int page);

}
