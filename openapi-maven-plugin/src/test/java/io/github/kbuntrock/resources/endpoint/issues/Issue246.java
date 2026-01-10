package io.github.kbuntrock.resources.endpoint.issues;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("logs")
public interface Issue246 {

	@GetMapping(value = { "/", "", "list" })
	List<String> getLogs();
}
