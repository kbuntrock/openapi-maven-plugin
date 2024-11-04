package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.issue144.ChildRequestDtoInterface;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Issue 144 webservice (by interface)
 */
@RequestMapping("/issue144")
public interface Issue144ByInterface {

	/**
	 * Get the requested items
	 * @param priceRequest some price request
	 * @return the requested item
	 */
	@PutMapping(path = "/foo")
	String getRequestItems(@RequestBody final ChildRequestDtoInterface priceRequest);

}
