package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {

	/**
	 * http code or default
	 */
	@JsonIgnore
	private Object code;
	private String description;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private final Map<String, Content> content = new LinkedHashMap<>();

	public Response() {
	}

	public Map<String, Content> getContent() {
		return content;
	}

	public Object getCode() {
		return code;
	}

	public void setCode(Integer code, String defaultSuccessfulOperationDescription) {
		if(code >= 200 && code < 300) {
			this.description = defaultSuccessfulOperationDescription;
		}
		// OpenAPI 3.0 requires response codes to be strings.
		this.code = String.valueOf(code);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
