package com.github.kbuntrock.configuration;

import com.github.kbuntrock.model.OperationType;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {

    @Parameter
    private String defaultConsumes = MediaType.APPLICATION_JSON_VALUE;

    @Parameter
    private String defaultProduces = MediaType.APPLICATION_JSON_VALUE;

    @Parameter
    private List<Substitution> substitutions = new ArrayList<>();

    public String getDefaultConsumes() {
        return defaultConsumes;
    }

    public void setDefaultConsumes(String defaultConsumes) {
        this.defaultConsumes = defaultConsumes;
    }

    public String getDefaultProduces() {
        return defaultProduces;
    }

    public void setDefaultProduces(String defaultProduces) {
        this.defaultProduces = defaultProduces;
    }

    private Map<String, List<Substitution>> substitutionsMap;

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }

    /**
     * Get substitutions by type
     *
     * @param type
     * @return substitutions to apply to the operationId
     */
    public List<Substitution> getSubstitutionsByType(String type) {
        if (substitutionsMap == null) {
            substitutionsMap = new HashMap<>();
            for (OperationType opType : OperationType.values()) {
                substitutionsMap.put(opType.toString().toLowerCase(), new ArrayList<>());
            }
            for (Substitution substitution : substitutions) {
                if (substitution.getType() == null) {
                    for (List<Substitution> value : substitutionsMap.values()) {
                        value.add(substitution);
                    }
                } else {
                    List<Substitution> list = substitutionsMap.get(substitution.getType().toLowerCase());
                    // It can only be null if there is a mistake in the configuration. It should be handled before.
                    if (list != null) {
                        list.add(substitution);
                    }
                }
            }

        }
        return substitutionsMap.get(type);
    }

    public void setSubstitutions(List<Substitution> substitutions) {
        this.substitutions = substitutions;
    }
}
