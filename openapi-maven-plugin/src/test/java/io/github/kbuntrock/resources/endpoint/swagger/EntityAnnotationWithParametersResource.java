package io.github.kbuntrock.resources.endpoint.swagger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api")
public class EntityAnnotationWithParametersResource {

	@Operation(summary = "Search alarms", parameters = {
			@Parameter(name = "pv", description = "PV name", in = ParameterIn.QUERY, schema = @Schema(type = "string"), required = false, example = "*"),
	})
	@Parameters({
			@Parameter(name = "start", description = "Start time", schema = @Schema(type = "string"), required = false, example = "2024-06-12"),
			@Parameter(name = "end", description = "End time", schema = @Schema(type = "string"), required = false, example = "2024-06-14"),
	})
	@RequestMapping(value = "/search/alarm", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntityWithAnnotations> searchAlarms() {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());

	}

	@Operation(summary = "Search alarms by PV name")
	@RequestMapping(value = "/search/alarm/pv/{pv}", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntityWithAnnotations> searchPv(
		@Parameter(description = "PV name", name = "pv") @PathVariable String pv) {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());

	}

	@Operation(summary = "Search alarms by trigger")
	@RequestMapping(value = "/search/alarm/trigger/{trigger}", method = RequestMethod.GET)
	public ResponseEntity<ResponseEntityWithAnnotations> searchTrigger(
		@Parameter(description = "Trigger of the alarms", example = "rule47") @PathVariable("trigger") String triggerParam) {
		return ResponseEntity.ok(new ResponseEntityWithAnnotations());
	}
}
