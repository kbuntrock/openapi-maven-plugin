package io.github.kbuntrock.resources.endpoint.swagger;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.NumberDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "SwaggerTagName", description = "Swagger Tag Description")
@RequestMapping(Constants.BASE_API + "/swagger")
public interface SwaggerController {

	@Operation(summary = "Operation summary", description = "Operation description", deprecated = true,
			externalDocs = @ExternalDocumentation(description = "External docs description", url = "External docs URL"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "successful operation")})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	boolean getWhatever();
}
