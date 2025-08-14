package io.github.kbuntrock.model.annotation;

import io.github.kbuntrock.model.DataObject;

public class OperationResponse {

	private Integer code;
	private String description;
	private DataObject dataObject;

	public OperationResponse() {
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataObject getDataObject() {
		return dataObject;
	}

	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}
}
