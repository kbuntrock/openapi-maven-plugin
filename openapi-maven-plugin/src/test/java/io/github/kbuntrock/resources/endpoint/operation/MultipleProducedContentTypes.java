package io.github.kbuntrock.resources.endpoint.operation;

import io.github.kbuntrock.resources.dto.AccountDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A controller producing multiple content types
 */
@RequestMapping("/multiple-produced-content-types")
public interface MultipleProducedContentTypes {

	/**
	 * produce some json
	 * @param firstname a given firstname
	 * @return
	 */
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	AccountDto json(@RequestParam(required = false) String firstname);

	/**
	 * Produce some xml
	 * @param surname a given surname
	 * @return
	 */
	@GetMapping(path = "/", produces = MediaType.APPLICATION_XML_VALUE)
	AccountDto xml(@RequestParam(required = false) String surname);
}