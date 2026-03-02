package io.github.kbuntrock.resources.endpoint.jackson.inputoutput;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.OrderJacksonDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller named JacksonController2
 */
@RequestMapping(Constants.BASE_API + "/2")
@RestController
public interface JacksonController2 {

	/**
	 * Create an order
	 *
	 * @param dto
	 *            the order to create
	 */
	@PostMapping()
	void create(@RequestBody OrderJacksonDto dto);
}
