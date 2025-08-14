package io.github.kbuntrock.model.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Store everything extracted from io.swagger.v3.oas.annotations.Operation or similar annotation
 */
public class OperationAnnotationInfo {

	private String operationId;

	private String summary;

	private String description;

	private List<OperationResponse> responses = new ArrayList<>();

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

	public List<OperationResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<OperationResponse> responses) {
		this.responses = responses;
	}

	public void addResponse(OperationResponse response) {
		this.responses.add(response);
	}
}
