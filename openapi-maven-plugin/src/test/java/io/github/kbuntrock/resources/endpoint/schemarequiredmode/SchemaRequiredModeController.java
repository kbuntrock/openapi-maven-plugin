package io.github.kbuntrock.resources.endpoint.schemarequiredmode;

import io.github.kbuntrock.resources.dto.schemarequiredmode.SchemaRequiredModeDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/schema-required-mode")
public interface SchemaRequiredModeController {

	@GetMapping("/{id}")
	SchemaRequiredModeDto getById(@PathVariable(value = "id") Long id);

	@GetMapping("/by-email")
	SchemaRequiredModeDto getByEmail(
		@Parameter(schema = @Schema(format = "email")) @RequestParam(value = "email") String email);
}
