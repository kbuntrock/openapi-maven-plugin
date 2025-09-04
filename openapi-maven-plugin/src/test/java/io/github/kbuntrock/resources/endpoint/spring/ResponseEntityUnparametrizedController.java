package io.github.kbuntrock.resources.endpoint.spring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller designed to test unparametrized response entities
 */
@RequestMapping("api")
public interface ResponseEntityUnparametrizedController {

	@GetMapping("information")
	ResponseEntity getInformation();

}
