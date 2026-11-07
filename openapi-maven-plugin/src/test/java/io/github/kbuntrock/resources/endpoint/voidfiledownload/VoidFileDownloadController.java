package io.github.kbuntrock.resources.endpoint.voidfiledownload;

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
 * File download endpoints returning void and streaming the body directly to the servlet response (issue #439).
 */
@RestController
@RequestMapping("/void-file-download")
public class VoidFileDownloadController {

	@ApiResponse(responseCode = "200", description = "The file content", content = @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = byte[].class)))
	@GetMapping(value = "/explicit-media-type/{id}", produces = "application/octet-stream")
	public void downloadWithExplicitMediaType(@PathVariable final String id, final HttpServletResponse response)
		throws IOException {
		response.getOutputStream().write(new byte[0]);
	}

	// The @Content media type takes precedence over the endpoint "produces" formats.
	@ApiResponse(responseCode = "200", description = "The PDF report", content = @Content(mediaType = "application/pdf", schema = @Schema(implementation = byte[].class)))
	@GetMapping(value = "/media-type-precedence/{id}", produces = "application/octet-stream")
	public void downloadWithMediaTypeOverridingProduces(@PathVariable final String id, final HttpServletResponse response)
		throws IOException {
		response.getOutputStream().write(new byte[0]);
	}

	// No media type on @Content: the endpoint "produces" formats are used.
	@ApiResponse(responseCode = "200", description = "The file content", content = @Content(schema = @Schema(implementation = byte[].class)))
	@GetMapping(value = "/produces-fallback/{id}", produces = "application/octet-stream")
	public void downloadWithProducesFallback(@PathVariable final String id, final HttpServletResponse response)
		throws IOException {
		response.getOutputStream().write(new byte[0]);
	}

	// No @ApiResponse: a void method keeps documenting a 200 response without content.
	@GetMapping(value = "/undocumented/{id}", produces = "application/octet-stream")
	public void downloadUndocumented(@PathVariable final String id, final HttpServletResponse response)
		throws IOException {
		response.getOutputStream().write(new byte[0]);
	}
}
