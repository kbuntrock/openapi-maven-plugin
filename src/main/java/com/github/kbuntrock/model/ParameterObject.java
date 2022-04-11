package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.ParameterLocation;

public class ParameterObject extends DataObject {

    private String name;
    private boolean required;
    private ParameterLocation location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ParameterLocation getLocation() {
        return location;
    }

    public void setLocation(ParameterLocation location) {
        this.location = location;
    }
}
