package com.github.kbuntrock.model;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.configuration.Substitution;

import java.util.List;

public class Endpoint {

    private String path;

    private OperationType type;

    private String name;

    private String computedName;

    private List<ParameterObject> parameters;

    private Integer responseCode;
    private DataObject responseObject;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String computeConfiguredName(ApiConfiguration apiConfiguration) {
        if (computedName == null) {
            computedName = getName();
            for (Substitution substitution : apiConfiguration.getOperation().getSubstitutionsByType(getType().toString().toLowerCase())) {
                computedName = computedName.replaceAll(substitution.getRegex(), substitution.getSubstitute());
            }
        }
        return computedName;
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
