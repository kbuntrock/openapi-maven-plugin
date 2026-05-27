package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityScheme {

	private String type;
	private String description;
	private String name;
	private String in;
	private String scheme;
	private String bearerFormat;
	private String openIdConnectUrl;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/** For serialization: only emit "name" when type is apiKey */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getName() {
		return "apiKey".equals(type) ? name : null;
	}

	/** For internal use (map keys, equals/hashCode) */
	@com.fasterxml.jackson.annotation.JsonIgnore
	public String getRawName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getIn() {
		return "apiKey".equals(type) ? in : null;
	}

	public void setIn(String in) {
		this.in = in;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getScheme() {
		return "http".equals(type) ? scheme : null;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getBearerFormat() {
		return "http".equals(type) ? bearerFormat : null;
	}

	public void setBearerFormat(String bearerFormat) {
		this.bearerFormat = bearerFormat;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getOpenIdConnectUrl() {
		return "openIdConnect".equals(type) ? openIdConnectUrl : null;
	}

	public void setOpenIdConnectUrl(String openIdConnectUrl) {
		this.openIdConnectUrl = openIdConnectUrl;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		SecurityScheme that = (SecurityScheme) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
