package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.issue144.ChildRequestDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * Issue 144 webservice
 */
@RequestMapping("/issue144")
public interface Issue144 {

	/**
	 * Get the requested items
	 * @param priceRequest some price request
	 * @return the requested item
	 */
	@PutMapping(path = "/foo")
	String getRequestItems(@RequestBody final ChildRequestDto priceRequest);

}
