package io.github.kbuntrock.model;

import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.Substitution;

import java.util.List;

public class Endpoint {

    private String path;

    private OperationType type;

    private String name;

    private String computedName;

    private List<ParameterObject> parameters;

    private Integer responseCode;
    private DataObject responseObject;
    private String responseFormat;
    
    private boolean deprecated = false;

    /**
     * Used to identify uniquely a endpoint. Aggregation of the returned type, the name and the parameters types.
     */
    private String identifier;

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

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
