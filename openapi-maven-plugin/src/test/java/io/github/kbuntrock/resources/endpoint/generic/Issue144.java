package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.issue144.ChildRequestDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/issue144")
public interface Issue144 {

	@PutMapping(path = "/foo")
	String getRequestItems(@Valid @RequestBody final ChildRequestDto priceRequest);

}
