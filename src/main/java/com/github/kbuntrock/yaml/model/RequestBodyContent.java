package com.github.kbuntrock.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBodyContent {

    private Map<String, Object> schema = new LinkedHashMap<>();

    public Map<String, Object> getSchema() {
        return schema;
    }
}
