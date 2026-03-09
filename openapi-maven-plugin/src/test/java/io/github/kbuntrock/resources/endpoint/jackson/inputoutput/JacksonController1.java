package io.github.kbuntrock.resources.endpoint.jackson.inputoutput;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.OrderJacksonDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A controller named JacksonController1
 */
@RequestMapping(Constants.BASE_API + "/1")
@RestController
public interface JacksonController1 {

	/**
	 * Find all orders
	 *
	 * @return all the orders
	 */
	@RequestMapping(method = RequestMethod.GET)
	List<OrderJacksonDto> findAll();
}
