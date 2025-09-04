package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class EntityAnnotationResource {


	@Operation(summary = "Swagger summary", method = "GET")
	@GetMapping(value = "summary")
	public ResponseEntity<ResponseEntityWithAnnotations> swaggerSummary() {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());

	}

	@Operation(summary = "Swagger summary",
			operationId = "different_errors",
			responses = {
					@ApiResponse(responseCode = "200", description = "Swagger Successful operation"),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorEntityWithAnnotations.class))),
					@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorEntityWithAnnotations.class)))
			})
	@GetMapping("/different_errors")
	public ResponseEntity<ResponseEntityWithAnnotations> errorResponses() {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());
	}

	@Operation(summary = "Swagger summary of unparametrized operation",
			responses = {
					@ApiResponse(responseCode = "200", description = "The success response", content = @Content(schema = @Schema(implementation = SuccessEntity.class))),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorEntityWithAnnotations.class))),
					@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorEntityWithAnnotations.class)))
			})
	@GetMapping("/unparametrized")
	public ResponseEntity unparametrized() {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());
	}

}
