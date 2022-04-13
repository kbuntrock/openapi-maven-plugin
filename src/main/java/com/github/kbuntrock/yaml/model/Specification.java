package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"openapi", "info", "servers","tags", "paths", "components" })
public class Specification {

    private String openapi = "3.0.3";

    private Info info;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Server> servers = new ArrayList<>();

    private List<TagElement> tags;

    private Map<String, Map<String, Operation>> paths;

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

    public List<Server> getServers() {
        return servers;
    }

    public List<TagElement> getTags() {
        return tags;
    }

    public void setTags(List<TagElement> tags) {
        this.tags = tags;
    }

    public Map<String, Map<String, Operation>> getPaths() {
        return paths;
    }

    public void setPaths(Map<String, Map<String, Operation>> paths) {
        this.paths = paths;
    }

    public Map<String, Object> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Object> components) {
        this.components = components;
    }

}
