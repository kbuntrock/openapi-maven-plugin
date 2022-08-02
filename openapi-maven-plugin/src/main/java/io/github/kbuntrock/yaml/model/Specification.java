package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"openapi", "info", "servers", "tags", "paths", "components"})
public class Specification {

    private String openapi = "3.0.3";

    private Info info;

    /**
     * An array of server. If handled by default, it will be a list of one element. Either way, it will be a JsonNode
     * given by a user json configuration
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object servers;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TagElement> tags;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Map<String, Operation>> paths;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> components = new LinkedHashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object security;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object externalDocs;

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

    public Object getServers() {
        return servers;
    }

    public void setServers(Object servers) {
        this.servers = servers;
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

    public Object getSecurity() {
        return security;
    }

    public void setSecurity(Object security) {
        this.security = security;
    }

    public Object getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(Object externalDocs) {
        this.externalDocs = externalDocs;
    }
}
