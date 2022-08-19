package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.kbuntrock.model.DataObject;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property extends Schema {

    @JsonIgnore
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private Integer minLength;
    private Integer maxLength;
    private Boolean uniqueItems;
    @JsonIgnore
    private boolean required;

    @JsonIgnore
    private DataObject parentDataObject;

    public Property() {
        super();
    }

    public Property(Schema schema) {
        this.setProperties(schema.getProperties());
        this.setAdditionalProperties(schema.getAdditionalProperties());
        this.setItems(schema.getItems());
        this.setType(schema.getType());
        this.setFormat(schema.getFormat());
        this.setRequired(schema.getRequired());
        this.setReference(schema.getReference());
        this.setEnumValues(schema.getEnumValues());
    }

    public Property(DataObject dataObject, boolean mainReference, String name, Set<String> exploredSignatures, DataObject parentDataObject) {
        super(dataObject, mainReference, exploredSignatures, parentDataObject, name);
        if (dataObject.isOpenApiArray()) {
            this.setUniqueItems(true);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUniqueItems() {
        return uniqueItems;
    }

    public void setUniqueItems(Boolean uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
