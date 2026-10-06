package io.github.kbuntrock.resources.endpoint.issues.issue460;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("dummy")
public class HiddenController {

	@GetMapping("resource")
	public ResponseEntity<String> getResource() {
		return ResponseEntity.ok().build();
	}
}
