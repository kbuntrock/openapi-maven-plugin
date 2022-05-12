package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, Content> content = new LinkedHashMap<>();

    public Map<String, Content> getContent() {
        return content;
    }

}
