package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Specification {

    private String openapi = "3.0.3";

    private Info info;

    private List<TagElement> tagElements;

    private Map<String, Path> paths;

    private Map<String, Object> components = new HashMap<>();

    public String getOpenapi() {
        return openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Map<String, Path> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Path> paths) {
        this.paths = paths;
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Object> components) {
        this.components = components;
    }

    public List<TagElement> getTags() {
        return tagElements;
    }

    public void setTags(List<TagElement> tagElements) {
        this.tagElements = tagElements;
    }
}
