package com.github.kbuntrock.utils;

import java.util.List;
import java.util.Set;

public enum OpenApiDataType {
    STRING("string"),
    INTEGER("integer"),
    NUMBER("number"),
    ARRAY("array"),
    OBJECT("object");

    private String apiName;

    private OpenApiDataType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public static OpenApiDataType fromJavaType(Class<?> type) {
        if (Integer.class == type || Integer.TYPE == type) {
            return INTEGER;
        } else if (Float.class == type || Float.TYPE == type
                || Double.class == type || Double.TYPE == type) {
            return NUMBER;
        } else if (String.class == type) {
            return STRING;
        } else if (type.isArray() || List.class == type || Set.class == type) {
            return ARRAY;
        }
        return OBJECT;
    }
}
