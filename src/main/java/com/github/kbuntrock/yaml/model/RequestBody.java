package com.github.kbuntrock.yaml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {

    private Map<String, RequestBodyContent> content = new LinkedHashMap<>();

    public Map<String, RequestBodyContent> getContent() {
        return content;
    }
    
}
