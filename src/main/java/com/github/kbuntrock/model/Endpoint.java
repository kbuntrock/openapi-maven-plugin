package com.github.kbuntrock.model;

import java.util.ArrayList;
import java.util.List;

public class Endpoint {

    private String path;

    private Operation operation;

    private String name;

    private List<ParameterObject> parameters;

    private Integer responseCode;
    private DataObject responseObject;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParameterObject> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterObject> parameters) {
        this.parameters = parameters;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public DataObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(DataObject responseObject) {
        this.responseObject = responseObject;
    }

}
