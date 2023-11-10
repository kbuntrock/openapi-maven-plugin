package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.kbuntrock.model.DataObject;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property extends Schema {

	@JsonIgnore
	private String name;
	@JsonIgnore
	private Integer minLength;
	@JsonIgnore
	private Integer maxLength;
	@JsonIgnore
	private Boolean uniqueItems;
	@JsonIgnore
	private boolean required;

	@JsonIgnore
	private DataObject parentDataObject;

	public Property() {
		super();
	}

	public Property(final Schema schema) {
		this.setProperties(schema.getProperties());
		this.setAdditionalProperties(schema.getAdditionalProperties());
		this.setItems(schema.getItems());
		this.setType(schema.getType());
		this.setRequired(schema.getRequired());
		this.setReference(schema.getReference());
		this.setEnumValues(schema.getEnumValues());
	}

	public Property(final DataObject dataObject, final boolean mainReference, final String name, final Set<String> exploredSignatures,
		final DataObject parentDataObject) {
		super(dataObject, mainReference, exploredSignatures, parentDataObject, name);
		if(dataObject.isOpenApiArray()) {
			this.setUniqueItems(true);
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	public void setUniqueItems(final Boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(final Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(final Integer maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	@JsonAnyGetter
	public Map<String, Object> getJsonObject() {

		final Map<String, Object> map = super.getJsonObject();
		if(minLength != null) {
			map.put("minLength", minLength);
		}
		if(maxLength != null) {
			map.put("maxLength", maxLength);
		}
		if(uniqueItems != null) {
			map.put("uniqueItems", uniqueItems);
		}
		return map;
	}
}
