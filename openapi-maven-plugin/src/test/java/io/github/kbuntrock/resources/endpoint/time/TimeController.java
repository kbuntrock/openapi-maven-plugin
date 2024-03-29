package io.github.kbuntrock.resources.endpoint.time;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.TimeDtoV2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Constants.BASE_API + "/time")
public interface TimeController {

	@GetMapping("/get-timedto")
	TimeDtoV2 getTimeDto();

}
