package io.github.kbuntrock.resources.endpoint.customresponsetype;

import io.github.kbuntrock.resources.dto.KeyAndPasswordDto;
import io.github.kbuntrock.resources.endpoint.jaxrs.ResponseType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring MVC endpoints using the "customResponseTypeAnnotation" option (issue #439).
 */
@RestController
@RequestMapping("/custom-response-type")
public class CustomResponseTypeSpringController {

	// void method streaming a file: the annotation type is documented, with the "produces" media type.
	@ResponseType(byte[].class)
	@GetMapping(value = "/void-download/{id}", produces = "application/octet-stream")
	public void voidDownload(@PathVariable final String id, final HttpServletResponse response) throws IOException {
		response.getOutputStream().write(new byte[0]);
	}

	// The annotation type replaces the declared return type.
	@ResponseType(KeyAndPasswordDto.class)
	@GetMapping("/overridden-return-type")
	public String overriddenReturnType() {
		return "{}";
	}

	// No annotation: a void method keeps documenting a 200 response without content.
	@GetMapping(value = "/void-not-annotated/{id}", produces = "application/octet-stream")
	public void voidNotAnnotated(@PathVariable final String id, final HttpServletResponse response) throws IOException {
		response.getOutputStream().write(new byte[0]);
	}

	// Both the custom annotation and an explicit @ApiResponse(200) with @Content: the explicit content replaces the one
	// generated from the annotation type, so only "application/pdf" is documented.
	@ResponseType(byte[].class)
	@ApiResponse(responseCode = "200", description = "The PDF report", content = @Content(mediaType = "application/pdf", schema = @Schema(implementation = KeyAndPasswordDto.class)))
	@GetMapping(value = "/with-api-response/{id}", produces = "application/octet-stream")
	public void withApiResponse(@PathVariable final String id, final HttpServletResponse response) throws IOException {
		response.getOutputStream().write(new byte[0]);
	}
}
