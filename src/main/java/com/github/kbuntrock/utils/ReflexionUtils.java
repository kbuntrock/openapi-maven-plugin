package com.github.kbuntrock.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReflexionUtils {

    private ReflexionUtils() {
    }

    public static List<Field> getAllNonStaticFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            getAllNonStaticFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()).stream()
                .filter(x -> !Modifier.isStatic(x.getModifiers())).collect(Collectors.toList()));

        return fields;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return fields;
    }
}
