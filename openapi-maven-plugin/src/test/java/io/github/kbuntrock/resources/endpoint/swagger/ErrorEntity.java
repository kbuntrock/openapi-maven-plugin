package io.github.kbuntrock.resources.endpoint.swagger;

import java.time.LocalDateTime;

public class ErrorEntity {

	private LocalDateTime timestamp;
	private String sessionId;
	private String correlationId;
	private String requestId;
	private String errorId;
	private String errorCode;
	private Integer status;
	private String error;
	private String message;
	private String path;
	private Object sourceInfo;
	private ErrorEntity cause;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getErrorId() {
		return errorId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	public Object getSourceInfo() {
		return sourceInfo;
	}

	public ErrorEntity getCause() {
		return cause;
	}

}
