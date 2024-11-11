package io.github.kbuntrock.resources.endpoint.operation;

import io.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller producing multiple content types, with an error in the parameters
 */
@RequestMapping("/multiple-produced-content-types")
public interface MultipleProducedContentTypesParameterIncoherence {

	/**
	 * Get some value
	 * @param firstname a given firstname
	 * @param surname a mistake in a parameter
	 * @return
	 */
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	AccountDto get(@RequestParam(required = false) String firstname, @RequestParam(required = false) Long surname);

	/**
	 * Produce some xml
	 * @param surname a given surname
	 * @param gender the gender
	 * @param age current age
	 * @return
	 */
	@GetMapping(path = "/", produces = MediaType.APPLICATION_XML_VALUE)
	AccountDto xml(@RequestParam(required = false) String surname, @RequestParam(required = false) String gender,
		@RequestParam(required = false) Integer age);
}