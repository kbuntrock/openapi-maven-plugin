package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {

    public static String DEFAULT_CODE = "default";
    public static String SUCCESSFUL_OPERATION = "successful operation";

    /**
     * http code or default
     */
    @JsonIgnore
    private Object code;
    private String description;

    private Map<String, Content> content = new LinkedHashMap<>();

    public Response() {
    }

    public Map<String, Content> getContent() {
        return content;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
        if(code instanceof Integer){
            if((Integer) code >= 200 && (Integer) code < 300){
                this.description = SUCCESSFUL_OPERATION;
            }
        }
    }

    public String getDescription() {
        return description;
    }

}
