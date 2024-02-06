package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ExternalDocs {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String url;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String description;

    public ExternalDocs(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }
}
