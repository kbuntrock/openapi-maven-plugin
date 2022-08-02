package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TagElement {

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    public TagElement() {

    }

    public TagElement(String name, String description) {
        this.name = name;
        this.description = description;
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
}
