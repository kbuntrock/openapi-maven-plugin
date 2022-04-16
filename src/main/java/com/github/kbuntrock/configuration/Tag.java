package com.github.kbuntrock.configuration;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    @Parameter(required = false)
    private List<Substitution> substitutions = new ArrayList<>();

    public List<Substitution> getSubstitutions() {
        return substitutions;
    }

    public void setSubstitutions(List<Substitution> substitutions) {
        this.substitutions = substitutions;
    }
}
