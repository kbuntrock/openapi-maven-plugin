package io.github.kbuntrock.resources.endpoint.explicitstatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/explicit-status")
public class ExplicitStatusController {

	// No @ResponseStatus: the real status (201) is only known through @ApiResponse, set
	// programmatically at runtime via ResponseEntity.created(...). Must produce only a 201
	// response, not a spurious extra 200.
	@Operation(responses = {
			@ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = UUID.class)))
	})
	@PostMapping("/no-response-status-annotation")
	public ResponseEntity<UUID> createWithoutResponseStatusAnnotation() {
		return ResponseEntity.status(HttpStatus.CREATED).body(UUID.randomUUID());
	}

	// @ApiResponse documents the same code the implicit default would already use (200): the
	// existing merge-into-the-default-response behavior must still apply (single 200 entry, with
	// its content still filled from the method's actual return type).
	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Found")
	})
	@PostMapping("/matching-explicit-code")
	public UUID matchingExplicitCode() {
		return UUID.randomUUID();
	}

	// @ResponseStatus and @ApiResponse agree on the same non-200 code: must still produce a single
	// response entry, not a duplicate.
	@Operation(responses = {
			@ApiResponse(responseCode = "201", description = "Created")
	})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/matching-response-status")
	public UUID matchingResponseStatus() {
		return UUID.randomUUID();
	}

	// @ResponseStatus(CREATED) is the real, runtime-accurate status (201), but a stale/incorrect
	// @ApiResponse(responseCode="200") also documents the method. @ResponseStatus is ground truth
	// here, so both entries must be kept (mirroring what springdoc itself does for the same
	// annotation combination) rather than the 201 being silently dropped in favor of the stale 200.
	@Operation(responses = {
			@ApiResponse(responseCode = "200", description = "Stale, inconsistent with @ResponseStatus")
	})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/conflicting-stale-explicit-code")
	public UUID conflictingStaleExplicitCode() {
		return UUID.randomUUID();
	}
}
