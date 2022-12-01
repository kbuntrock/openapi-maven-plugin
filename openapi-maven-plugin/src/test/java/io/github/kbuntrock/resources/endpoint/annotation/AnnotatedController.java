package io.github.kbuntrock.resources.endpoint.annotation;

import io.github.kbuntrock.resources.annotation.MyRestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kevin Buntrock
 */
@MyRestController
public interface AnnotatedController {

	@GetMapping("counter")
	int getCounter();
}
