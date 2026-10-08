package io.github.kbuntrock.resources.endpoint.standaloneapiresponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/standalone-api-response")
public class StandaloneApiResponseController {

	// @ApiResponse used as a standalone, repeatable annotation directly on the method (not nested
	// inside @Operation(responses = {...})): must still be picked up, merged alongside the implicit
	// 200 default.
	@Operation(summary = "Get by id")
	@ApiResponse(responseCode = "404", description = "Not Found")
	@GetMapping("/{id}")
	public UUID getById(@PathVariable final String id) {
		return UUID.randomUUID();
	}

	// Two repeatable @ApiResponse occurrences, merged by the JVM into a synthetic @ApiResponses:
	// both must be picked up.
	@Operation(summary = "List")
	@ApiResponse(responseCode = "400", description = "Bad Request")
	@ApiResponse(responseCode = "404", description = "Not Found")
	@GetMapping
	public UUID list() {
		return UUID.randomUUID();
	}
}
