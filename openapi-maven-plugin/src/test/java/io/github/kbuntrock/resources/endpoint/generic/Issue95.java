package io.github.kbuntrock.resources.endpoint.generic;

import io.github.kbuntrock.resources.dto.genericity.issue95.ErrorDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kévin Buntrock
 */
@RequestMapping("/issue95")
public interface Issue95 {

	@PutMapping(path = "/error")
	ErrorDto error();

}
