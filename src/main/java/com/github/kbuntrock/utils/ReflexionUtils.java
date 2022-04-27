package com.github.kbuntrock.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class ReflexionUtils {

    private ReflexionUtils() {
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return fields;
    }
}
