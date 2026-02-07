package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Path("/api")
@Tag(name = "Response resource API", description = "Reference all endpoints linked to resources.")
public class ApiResponseResource {

	@Operation(summary = "Swagger summary", method = "GET")
	@GetMapping(value = "summary")
	@GET
	@Path("summary")
	public ResponseEntity<String> swaggerSummary() {
		return ResponseEntity.ok("returnValue");

	}

	@Operation(summary = "Swagger summary", operationId = "errorResponses", responses = {
			@ApiResponse(responseCode = "200", description = "Swagger Successful operation"),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorEntity.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
	})
	@GetMapping("/different_errors")
	@GET
	@Path("/different_errors")
	public ResponseEntity<String> errorResponses() {
		return ResponseEntity.ok("returnValue");
	}

	@Operation(summary = "Swagger summary", operationId = "errorResponsesWithNoAndErrorInResponseCode", responses = {
			@ApiResponse(description = "Swagger Successful operation"),
			@ApiResponse(responseCode = "NotANumber", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorEntity.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
	})
	@GetMapping("/responsecodeErrors")
	@GET
	@Path("/responsecodeErrors")
	public ResponseEntity<String> errorResponsesWithNoAndErrorInResponseCode() {
		return ResponseEntity.ok("returnValue");
	}
}
