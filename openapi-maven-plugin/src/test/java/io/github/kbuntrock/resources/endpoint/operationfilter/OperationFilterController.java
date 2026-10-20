package io.github.kbuntrock.resources.endpoint.operationfilter;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operation-filter")
public class OperationFilterController {

	@MyPublicApi
	@Operation(tags = "Custom Public Tag")
	@GetMapping("/public")
	public String getPublic() {
		return "public";
	}

	// Deliberately also carries an @Operation(tags=...): proves the override is gated on the
	// public-api check, not merely on the tags attribute being present.
	@Operation(tags = "Should Not Appear")
	@GetMapping("/internal")
	public String getInternal() {
		return "internal";
	}
}
