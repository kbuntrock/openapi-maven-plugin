package com.github.kbuntrock.model;

import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.configuration.Substitution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tag {

    private String name;
    private Class<?> clazz;

    private final List<Endpoint> endpoints = new ArrayList<>();

    private String computedName;

    public Tag(Class<?> clazz) {
        this.name = clazz.getSimpleName();
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public String computeConfiguredName(ApiConfiguration apiConfiguration) {
        if (computedName == null) {
            computedName = getName();
            for (Substitution substitution : apiConfiguration.getTag().getSubstitutions()) {
                computedName = computedName.replaceAll(substitution.getRegex(), substitution.getSubstitute());
            }
        }
        return computedName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void addEndpoint(final Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", endpoints=" + endpoints.stream().map(Endpoint::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
