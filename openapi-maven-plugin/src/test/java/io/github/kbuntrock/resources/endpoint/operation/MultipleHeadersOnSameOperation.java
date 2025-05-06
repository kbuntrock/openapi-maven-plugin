package io.github.kbuntrock.resources.endpoint.operation;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/roles-controller")
public class MultipleHeadersOnSameOperation {

	@GetMapping(value = "/roles", headers = "full=true", name = "getRoles")
	public ResponseEntity<String> getRoles(@RequestAttribute String user) {
		return ok().body("");
	}


	@GetMapping(value = "/roles", name = "getPaginatedRoles")
	public ResponseEntity<String> getPaginatedRoles(String pageable, @RequestAttribute String user) {
		return ok().body("");
	}

}
