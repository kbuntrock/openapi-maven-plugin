package io.github.kbuntrock.resources.endpoint.recursive;

import io.github.kbuntrock.resources.dto.recursive.RecursiveDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface RecursiveDtoController {

	@GetMapping("recursive")
	RecursiveDto getRecursive();

}
