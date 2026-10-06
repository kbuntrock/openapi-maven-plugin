package io.github.kbuntrock.resources.endpoint.issues.issue460;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dummy")
public class HiddenMethod {

	@GetMapping("resource")
	public ResponseEntity<String> getResource() {
		return ResponseEntity.ok().build();
	}

	@Hidden
	@GetMapping("hidden/resource")
	public ResponseEntity<String> getHiddenResource() {
		return ResponseEntity.ok().build();
	}
}
