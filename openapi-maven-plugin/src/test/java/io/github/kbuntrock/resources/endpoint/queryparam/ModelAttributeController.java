package io.github.kbuntrock.resources.endpoint.queryparam;

import io.github.kbuntrock.resources.dto.TimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api")
public interface ModelAttributeController {

	@GetMapping("is-time-valid")
	boolean isTimeValid(@ModelAttribute TimeDto time);

}
