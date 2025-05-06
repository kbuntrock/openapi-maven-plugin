package io.github.kbuntrock.resources.endpoint.operation;

import static org.springframework.http.ResponseEntity.ok;

import io.swagger.v3.oas.annotations.Operation;
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

	@Operation(operationId = "roles-operation-id", summary = "This is the summary.", description = "This is the description.")
	@GetMapping(value = "/roles", headers = "full=true", name = "getRoles")
	public ResponseEntity<String> getRoles(@RequestAttribute String user) {
		return ok().body("");
	}

	@Operation(operationId = "roles-operation-id", summary = "This is the summary.", description = "This is the description.")
	@GetMapping(value = "/roles", name = "getPaginatedRoles")
	public ResponseEntity<String> getPaginatedRoles(String pageable, @RequestAttribute String user) {
		return ok().body("");
	}

}
