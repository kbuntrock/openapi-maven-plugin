package io.github.kbuntrock.resources.endpoint.issues.issue412;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class Issue412Controller implements Issue412Api {

	@Override
	@GetMapping("/{name}")
	public String hello(@PathVariable String name) {
		return "Hello " + name;
	}
}
