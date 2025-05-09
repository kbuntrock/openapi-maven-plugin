package io.github.kbuntrock.model;

import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.ParameterLocation;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class ParameterObject extends DataObject {

	private String name;
	private boolean required;
	private boolean allowEmptyValue;
	private ParameterLocation location;
	// Set only if it is a "body" parameter : json, xml, plain text, ...
	private List<String> formats;

	/**
	 * For parameters fields extracted from a dto, save the origin class in order to retrieve the corresponding javadoc
	 *
	 * see SpringMvcReader#bindDtoToQueryParams
	 */
	private String javadocFieldClassName;

	/**
	 * Parameters name can be changed via annotation. Save the original one to find the corresponding javadoc.
	 */
	private String javadocFieldName;

	public ParameterObject(final String name, final Type type) {
		super(type);
		this.name = name;
		this.javadocFieldName = name;
	}

	public ParameterObject(final String name, final DataObject dataObject) {
		super(dataObject);
		this.name = name;
		this.javadocFieldName = name;
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

	public boolean isAllowEmptyValue() {
		return allowEmptyValue;
	}

	public void setAllowEmptyValue(boolean allowEmptyValue) {
		this.allowEmptyValue = allowEmptyValue;
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

	public String getJavadocFieldName() {
		return javadocFieldName;
	}

	public void setJavadocFieldName(final String javadocFieldName) {
		this.javadocFieldName = javadocFieldName;
	}

	public boolean isMultipartFile(){
		return MultipartFile.class == getJavaClass() ||
			(OpenApiDataType.ARRAY == getOpenApiResolvedType().getType()
				&& MultipartFile.class == getArrayItemDataObject().getJavaClass());
	}
}
