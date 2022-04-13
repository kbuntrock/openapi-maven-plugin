package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Operation {

    @JsonIgnore
    private String name;

    private List<String> tags = new ArrayList<>();

    private String operationId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ParameterElement> parameters = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RequestBody requestBody;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Map<Object, Response> responses = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
