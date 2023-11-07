package io.github.kbuntrock.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamSource;

public enum OpenApiDataType {

	STRING("string"),
	STRING_BINARY("string", OpenApiDataFormat.BINARY),
	STRING_DATE("string", OpenApiDataFormat.DATE),
	STRING_DATE_TIME("string", OpenApiDataFormat.DATE_TIME),
	STRING_TIME("string", OpenApiDataFormat.TIME),
	BOOLEAN("boolean"),
	INTEGER_32("integer", OpenApiDataFormat.INT32),
	INTEGER_64("integer", OpenApiDataFormat.INT64),
	NUMBER_FLOAT("number", OpenApiDataFormat.FLOAT),
	NUMBER_DOUBLE("number", OpenApiDataFormat.DOUBLE),
	ARRAY("array", OpenApiDataFormat.UNKNOWN),
	OBJECT("object", OpenApiDataFormat.UNKNOWN);

	private final String value;
	private final OpenApiDataFormat format;

	OpenApiDataType(final String openapiName) {
		this(openapiName, OpenApiDataFormat.NONE);
	}

	OpenApiDataType(final String value, final OpenApiDataFormat format) {
		this.value = value;
		this.format = format;
	}

    public static OpenApiDataType fromJavaClass(final Class<?> clazz, final Map<Class<?>, Class<?>> clazzMappers) {
        if (clazzMappers.containsKey(clazz)) {
            // this is to prevent infinite loop in the case of
            // clazzMappers = { A: B, B: A }
            Map<Class<?>, Class<?>> clazzMappersWithoutClass = new HashMap<>(clazzMappers);
            Class<?> newClazz = clazzMappersWithoutClass.remove(clazz);
            return fromJavaClass(newClazz, clazzMappersWithoutClass);
        }
		if(Boolean.class == clazz || Boolean.TYPE == clazz) {
			return BOOLEAN;
        }
        if (Integer.class == clazz || Integer.TYPE == clazz) {
            return INTEGER_32;
        }
        if (Long.class == clazz || Long.TYPE == clazz || BigInteger.class == clazz) {
            return INTEGER_64;
        }
        if (Float.class == clazz || Float.TYPE == clazz) {
            return NUMBER_FLOAT;
        }
        if (Double.class == clazz || Double.TYPE == clazz || BigDecimal.class == clazz) {
            return NUMBER_DOUBLE;
        }
        if (String.class == clazz) {
            return STRING;
        }
        if (LocalDateTime.class == clazz || Instant.class == clazz || Date.class == clazz) {
            return STRING_DATE_TIME;
        }
        if (LocalDate.class == clazz) {
            return STRING_DATE;
        }
        if (LocalTime.class == clazz) {
            return STRING_TIME;
        }
        if (InputStreamSource.class.isAssignableFrom(clazz) || InputStream.class.isAssignableFrom(clazz)) {
            return STRING_BINARY;
        }
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return ARRAY;
        }
        if (clazz.isEnum()) {
            return STRING;
        }
        return OBJECT;
	}

	public String getValue() {
		return value;
	}

	public OpenApiDataFormat getFormat() {
		return format;
	}

}
