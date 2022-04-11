package com.github.kbuntrock.utils;

import java.util.List;
import java.util.Set;

public enum OpenApiDataType {

    STRING("string"),
    BYTES("string", OpenApiDataFormat.BYTE),
    BINARY("string", OpenApiDataFormat.BINARY),
    BOOLEAN("boolean"),
    INTEGER_32("integer", OpenApiDataFormat.INT32),
    INTEGER_64("integer", OpenApiDataFormat.INT64),
    NUMBER_FLOAT("number", OpenApiDataFormat.FLOAT),
    NUMBER_DOUBLE("number", OpenApiDataFormat.DOUBLE),
    ARRAY("array", OpenApiDataFormat.UNKNOWN),
    OBJECT("object", OpenApiDataFormat.UNKNOWN);

    private String value;
    private OpenApiDataFormat format;

    private OpenApiDataType(String openapiName) {
        this(openapiName, OpenApiDataFormat.NONE);
    }

    private OpenApiDataType(String value, OpenApiDataFormat format) {
        this.value = value;
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public OpenApiDataFormat getFormat() {
        return format;
    }

    public static OpenApiDataType fromJavaType(Class<?> type) {
        if(Boolean.class == type || Boolean.TYPE == type){
            return BOOLEAN;
        } else if (Integer.class == type || Integer.TYPE == type) {
            return INTEGER_32;
        } else if (Long.class == type || Long.TYPE == type) {
            return INTEGER_64;
        } else if (Float.class == type || Float.TYPE == type) {
            return NUMBER_FLOAT;
        } else if (Double.class == type || Double.TYPE == type) {
            return NUMBER_DOUBLE;
        } else if (String.class == type) {
            return STRING;
        } else if (type.isArray() || List.class == type || Set.class == type) {
            return ARRAY;
        }
        return OBJECT;
    }
}
