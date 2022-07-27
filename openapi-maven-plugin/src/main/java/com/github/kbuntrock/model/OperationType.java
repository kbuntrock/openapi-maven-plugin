package com.github.kbuntrock.model;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public enum OperationType {
    GET(RequestMethod.GET),
    POST(RequestMethod.POST),
    PUT(RequestMethod.PUT),
    PATCH(RequestMethod.PATCH),
    DELETE(RequestMethod.DELETE),
    HEAD(RequestMethod.HEAD),
    OPTIONS(RequestMethod.OPTIONS),
    TRACE(RequestMethod.TRACE);

    private static final Map<RequestMethod, OperationType> mapBySpringMvcRequestMethod = new HashMap<>();

    static {
        for (OperationType type : OperationType.values()) {
            mapBySpringMvcRequestMethod.put(type.springMvcRequestMethod, type);
        }
    }

    private final RequestMethod springMvcRequestMethod;

    OperationType(RequestMethod springMvcRequestMethod) {
        this.springMvcRequestMethod = springMvcRequestMethod;
    }

    public static OperationType from(RequestMethod springMvcRequestMethod) {
        return mapBySpringMvcRequestMethod.get(springMvcRequestMethod);
    }
}
