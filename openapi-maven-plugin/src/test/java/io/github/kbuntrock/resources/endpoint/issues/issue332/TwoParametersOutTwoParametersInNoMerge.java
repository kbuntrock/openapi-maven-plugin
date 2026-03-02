package io.github.kbuntrock.resources.endpoint.issues.issue332;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/issue332")
public class TwoParametersOutTwoParametersInNoMerge {

	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Find all", description = "Retrieving all")
	@Parameter(name = "first", example = "hello", description = "Description.", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string"), array = @ArraySchema(schema = @Schema(type = "string")))
	@Parameter(name = "second", example = "hi", description = "Description.", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string"), array = @ArraySchema(schema = @Schema(type = "string")))

	public ResponseEntity<List<String>> getAll(
		@Nullable @Parameter(name = "sort") String sort,
		@Parameter(name = "count") int count) {

		return ResponseEntity.ok().build();

	}
}