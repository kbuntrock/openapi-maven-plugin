package io.github.kbuntrock.resources.endpoint.swagger;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ErrorEntityWithAnnotations {

	/**
	 * TimeStamp from javadoc, swagger should win as it is run last
	 */
	@Schema(description = "Timestamp of the error", example = "2023-10-01T12:00:00")
	private LocalDateTime timestamp;
	@Schema(description = "Session ID associated with the error", example = "session-12345")
	private String sessionId;
	@Schema(description = "Correlation ID for tracking the request", example = "correlation-67890")
	private String correlationId;
	@Schema(description = "Request ID for the operation", example = "request-54321")
	private String requestId;
	@Schema(description = "Unique identifier for the error", example = "error-98765")
	private String errorId;
	@Schema(description = "Error code representing the type of error", example = "ERR-001")
	private String errorCode;
	@Schema(description = "HTTP status code of the error", example = "500")
	private Integer status;
	@Schema(description = "Short description of the error", example = "Internal Server Error")
	private String error;
	@Schema(description = "Detailed message about the error", example = "An unexpected error occurred while processing the request.")
	private String message;
	@Schema(description = "Path of the request that caused the error", example = "/api/resource")
	private String path;
	/**
	 * Status of the response
	 */
	@Schema(example = "Additional information about the error")
	private String extraInfo;
	@Schema(description = "The cause of the error, if any")
	private ErrorEntityWithAnnotations cause;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ErrorEntityWithAnnotations getCause() {
		return cause;
	}

	public void setCause(ErrorEntityWithAnnotations cause) {
		this.cause = cause;
	}
}
