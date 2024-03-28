package io.github.kbuntrock.model;

import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.reflect.Type;
import java.util.List;

public class ParameterObject extends DataObject {

	private String name;
	private boolean required;
	private ParameterLocation location;
	// Set only if it is a "body" parameter : json, xml, plain text, ...
	private List<String> formats;

	/**
	 * For parameters fields extracted from a dto, save the origin class in order to retrieve the corresponding javadoc
	 *
	 * see SpringMvcReader#bindDtoToQueryParams
	 */
	private String javadocFieldClassName;

	public ParameterObject(final String name, final Type type) {
		super(type);
		this.name = name;
	}

	public ParameterObject(final String name, final DataObject dataObject) {
		super(dataObject);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	public ParameterLocation getLocation() {
		return location;
	}

	public void setLocation(final ParameterLocation location) {
		this.location = location;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(final List<String> formats) {
		this.formats = formats;
	}

	public String getJavadocFieldClassName() {
		return javadocFieldClassName;
	}

	public void setJavadocFieldClassName(final String javadocFieldClassName) {
		this.javadocFieldClassName = javadocFieldClassName;
	}
}
