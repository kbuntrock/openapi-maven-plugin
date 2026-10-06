package io.github.kbuntrock.resources.endpoint.pathcollision;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/path-collision")
public class PathCollisionController {

	@GetMapping
	public String getAll() {
		return "all";
	}

	@GetMapping(params = { "name", "!id" })
	public String getByName(@RequestParam String name) {
		return name;
	}

	@GetMapping(params = { "id", "!name" })
	public String getById(@RequestParam UUID id) {
		return id.toString();
	}

	@GetMapping(params = { "name", "id" })
	@Operation(hidden = true)
	public String getByNameAndId(@RequestParam String name, @RequestParam UUID id) {
		return name + id;
	}
}
