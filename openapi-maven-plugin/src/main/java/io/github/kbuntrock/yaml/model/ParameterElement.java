package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ParameterElement {

    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private String in;
    private boolean required;
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

    public Property getSchema() {
        return schema;
    }

    public void setSchema(Property schema) {
        this.schema = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
