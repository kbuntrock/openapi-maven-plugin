package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, Content> content = new LinkedHashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    public Map<String, Content> getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
