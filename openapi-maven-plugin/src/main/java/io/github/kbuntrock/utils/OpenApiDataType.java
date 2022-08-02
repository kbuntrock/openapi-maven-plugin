package io.github.kbuntrock.utils;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public enum OpenApiDataType {

    STRING("string"),
    STRING_BINARY("string", OpenApiDataFormat.BINARY),
    STRING_DATE("string", OpenApiDataFormat.DATE),
    STRING_DATE_TIME("string", OpenApiDataFormat.DATE_TIME),
    BOOLEAN("boolean"),
    INTEGER_32("integer", OpenApiDataFormat.INT32),
    INTEGER_64("integer", OpenApiDataFormat.INT64),
    NUMBER_FLOAT("number", OpenApiDataFormat.FLOAT),
    NUMBER_DOUBLE("number", OpenApiDataFormat.DOUBLE),
    ARRAY("array", OpenApiDataFormat.UNKNOWN),
    OBJECT("object", OpenApiDataFormat.UNKNOWN);

    private final String value;
    private final OpenApiDataFormat format;

    OpenApiDataType(String openapiName) {
        this(openapiName, OpenApiDataFormat.NONE);
    }

    OpenApiDataType(String value, OpenApiDataFormat format) {
        this.value = value;
        this.format = format;
    }

    public String getValue() {
        return value;
    }

    public OpenApiDataFormat getFormat() {
        return format;
    }

    public static OpenApiDataType fromJavaClass(Class<?> clazz) {
        if (Boolean.class == clazz || Boolean.TYPE == clazz) {
            return BOOLEAN;
        } else if (Integer.class == clazz || Integer.TYPE == clazz) {
            return INTEGER_32;
        } else if (Long.class == clazz || Long.TYPE == clazz || BigInteger.class == clazz) {
            return INTEGER_64;
        } else if (Float.class == clazz || Float.TYPE == clazz) {
            return NUMBER_FLOAT;
        } else if (Double.class == clazz || Double.TYPE == clazz || BigDecimal.class == clazz) {
            return NUMBER_DOUBLE;
        } else if (String.class == clazz) {
            return STRING;
        } else if (LocalDateTime.class == clazz || Instant.class == clazz) {
            return STRING_DATE_TIME;
        } else if (LocalDate.class == clazz) {
            return STRING_DATE;
        } else if (MultipartFile.class == clazz) {
            return STRING_BINARY;
        } else if (clazz.isArray() || List.class == clazz || Set.class == clazz) {
            return ARRAY;
        } else if (clazz.isEnum()) {
            return STRING;
        }
        return OBJECT;
    }

}
