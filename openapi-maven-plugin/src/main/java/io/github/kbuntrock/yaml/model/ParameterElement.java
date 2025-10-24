package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ParameterElement {

	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String summary;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;
	private String in;
	private boolean required;
	@JsonInclude(Include.NON_DEFAULT)
	private boolean allowEmptyValue;
	@JsonInclude(Include.NON_NULL)
	private Property schema;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}

	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
	}

	public Property getSchema() {
		return schema;
	}

	public void setSchema(Property schema) {
		this.schema = schema;
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
