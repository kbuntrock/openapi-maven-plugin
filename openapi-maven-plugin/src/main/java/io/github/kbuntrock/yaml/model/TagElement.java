package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TagElement {

	private String name;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ExternalDocs externalDocs;

	public TagElement(String name, String description, ExternalDocs externalDocs) {
		this.name = name;
		this.description = description;
		this.externalDocs = externalDocs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ExternalDocs getExternalDocs() {
		return externalDocs;
	}
}
