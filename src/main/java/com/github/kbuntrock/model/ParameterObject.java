package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.ParameterLocation;

public class ParameterObject extends DataObject {

    private String name;
    private boolean required;
    private ParameterLocation location;
    // Set only if it is a "body" parameter : json, xml, plain text, ...
    private String format;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
