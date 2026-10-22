package io.github.kbuntrock.model.annotation;

import io.github.kbuntrock.model.DataObject;

public class OperationRequestBody {

	private String description;
	private String mediaType;
	private DataObject dataObject;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public DataObject getDataObject() {
		return dataObject;
	}

	public void setDataObject(DataObject dataObject) {
		this.dataObject = dataObject;
	}
}
