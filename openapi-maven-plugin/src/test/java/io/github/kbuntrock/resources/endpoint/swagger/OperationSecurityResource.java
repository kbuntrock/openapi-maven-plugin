package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operation-security")
public class OperationSecurityResource {

	// Declared only via @Operation's own nested `security` attribute, not a standalone
	// @SecurityRequirement/@SecurityRequirements - previously never read at all.
	@Operation(security = @SecurityRequirement(name = "oauth2", scopes = "read"))
	@GetMapping("/nested")
	public String nested() {
		return "nested";
	}

	// Two methods colliding on the same path (merged into one operation), each declaring a
	// different nested security requirement: both must survive in the merged result.
	@Operation(security = @SecurityRequirement(name = "oauth2", scopes = "read"))
	@GetMapping(params = "byId")
	public String byId() {
		return "byId";
	}

	@Operation(security = @SecurityRequirement(name = "apiKey"))
	@GetMapping(params = "byName")
	public String byName() {
		return "byName";
	}
}
