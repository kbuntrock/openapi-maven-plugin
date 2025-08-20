package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ResponseEntityWithAnnotations {

	@Schema(description = "Unique identifier for the response", example = "12345")
	private String id;
	@Schema(description = "Status of the response", example = "success")
	private String status;
	@Schema(description = "Data returned in the response", example = "{\"key\":\"value\"}")
	private String data;
	@Schema(description = "Timestamp of the collected data", example = "2023-10-01T12:00:00")
	private LocalDateTime timestamp;



}
