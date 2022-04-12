package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class Operation {

    private List<TagElement> tags = new ArrayList<>();

    private String operationId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ParameterElement> parameters = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RequestBody requestBody;

    public List<TagElement> getTags() {
        return tags;
    }

    public void setTags(List<TagElement> tags) {
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
}
