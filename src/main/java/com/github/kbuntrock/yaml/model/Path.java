package com.github.kbuntrock.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Path {

    private Map<String, Operation> operations = new LinkedHashMap<>();

    public Map<String, Operation> getOperations() {
        return operations;
    }
}
