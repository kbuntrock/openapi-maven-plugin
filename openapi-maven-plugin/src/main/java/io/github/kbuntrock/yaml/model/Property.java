package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.configuration.OpenapiVersion;
import io.github.kbuntrock.model.DataObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
	private boolean required;
	@JsonIgnore
	private String example;
	@JsonIgnore
	private String[] examples;
	@JsonIgnore
	private ReadWriteRule readWriteRule;

	@JsonIgnore
	private DataObject parentDataObject;

	public Property(final Schema schema) {
		super(schema.context, schema.apiConfiguration);
		this.setProperties(schema.getProperties());
		this.setAdditionalProperties(schema.getAdditionalProperties());
		this.setItems(schema.getItems());
		this.setType(schema.getType());
		this.setRequired(schema.getRequired());
		this.setReference(schema.getReference());
		this.setEnumValues(schema.getEnumValues());
	}

	public Property(final DataObject dataObject, final boolean mainReference, final String name,
		final Set<String> exploredSignatures,
		final DataObject parentDataObject, final TagLibrary tagLibrary) {
		super(dataObject, mainReference, exploredSignatures, parentDataObject, name, tagLibrary);
		if(dataObject.getClassRequired() != null) {
			this.setRequired(dataObject.getClassRequired());
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
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
		if(ReadWriteRule.READ_ONLY == readWriteRule) {
			map.put("readOnly", true);
		}
		if(ReadWriteRule.WRITE_ONLY == readWriteRule) {
			map.put("writeOnly", true);
		}
		if(minLength != null) {
			map.put("minLength", minLength);
		}
		if(maxLength != null) {
			map.put("maxLength", maxLength);
		}
		if(example != null && apiConfiguration.getOpenapiVersion() == OpenapiVersion.V3_0) {
			// Only for Openapi v3.0
			map.put("example", example);
		} else if(example != null || (examples != null && examples.length > 0)) {
			// Openapi v3.1 and above, preferred way
			final Set<String> distinctExamples = new LinkedHashSet<>();
			if(example != null) {
				distinctExamples.add(example);
			}
			if(examples != null) {
				for(final String value : examples) {
					if(value != null) {
						distinctExamples.add(value);
					}
				}
			}
			map.put("examples", new ArrayList<>(distinctExamples));
		}

		return map;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String[] getExamples() {
		return examples;
	}

	public void setExamples(String[] examples) {
		this.examples = examples;
	}

	public ReadWriteRule getReadWriteRule() {
		return readWriteRule;
	}

	public void setReadWriteRule(ReadWriteRule readWriteRule) {
		this.readWriteRule = readWriteRule;
	}
}
