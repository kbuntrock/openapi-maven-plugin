package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Operation {

    @JsonIgnore
    private String name;

    @JsonIgnore
    private String path;

    private List<String> tags = new ArrayList<>();

    private String operationId;

    @JsonProperty("x-operation-name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String loopbackOperationName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean deprecated;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ParameterElement> parameters = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RequestBody requestBody;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<Object, Response> responses = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<ParameterElement> getParameters() {
        return parameters;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public Map<Object, Response> getResponses() {
        return responses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getLoopbackOperationName() {
        return loopbackOperationName;
    }

    public void setLoopbackOperationName(String loopbackOperationName) {
        this.loopbackOperationName = loopbackOperationName;
    }
}
