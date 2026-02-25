package io.github.kbuntrock.resources.endpoint.jackson.inputoutput;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.AccountJacksonDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller named JacksonController3
 */
@RequestMapping(Constants.BASE_API + "/3")
@RestController
public interface JacksonController3 {

	/**
	 * Create an AccountJacksonDto
	 *
	 * @param account
	 *            the account to be created
	 */
	@PostMapping()
	void create(@RequestBody AccountJacksonDto account);

}
