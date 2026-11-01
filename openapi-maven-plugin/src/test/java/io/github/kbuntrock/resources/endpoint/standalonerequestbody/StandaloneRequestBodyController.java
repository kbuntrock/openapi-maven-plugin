package io.github.kbuntrock.resources.endpoint.standalonerequestbody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/standalone-request-body")
public class StandaloneRequestBodyController {

	// Documentation-only stub: no real @RequestBody parameter at all (the actual request handling
	// lives outside normal method dispatch, e.g. a security filter) - the entire request body must
	// come from the standalone @Operation(requestBody = @RequestBody(...)) attribute.
	@Operation(summary = "Generate token", requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = TokenRequest.class))))
	@PostMapping("/token")
	public void token() {
		// Real handling is outside Spring MVC method dispatch.
	}

	// Documentation-only stub declaring a media type but no schema: the media type must still be
	// documented (with an empty media type object), since "content" is mandatory in a request body.
	@Operation(summary = "Upload without schema", requestBody = @RequestBody(description = "Raw upload", content = @Content(mediaType = "application/octet-stream")))
	@PostMapping("/no-schema")
	public void noSchema() {
		// Real handling is outside Spring MVC method dispatch.
	}

	// A real @RequestBody parameter is present: it must win over the (deliberately conflicting)
	// standalone annotation, which must be ignored entirely in this case.
	@Operation(summary = "Should be ignored", requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = TokenRequest.class))))
	@PostMapping("/real-body-wins")
	public void realBodyWins(
		@org.springframework.web.bind.annotation.RequestBody final RealBodyRequest body) {
		// no-op
	}
}
