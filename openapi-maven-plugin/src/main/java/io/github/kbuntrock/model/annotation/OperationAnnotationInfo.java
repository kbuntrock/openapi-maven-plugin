package io.github.kbuntrock.model.annotation;

/**
 * Store everything extracted from io.swagger.v3.oas.annotations.Operation or similar annotation
 */
public class OperationAnnotationInfo {

	private String operationId;

	private String summary;

	private String description;

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
