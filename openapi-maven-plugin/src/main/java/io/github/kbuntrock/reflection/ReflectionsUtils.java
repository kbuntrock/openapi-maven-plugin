package io.github.kbuntrock.reflection;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ReflectionsUtils {

    private static boolean initiated = false;
    private static boolean testMode = false;
    private static ClassLoader projectClassLoader;

    private ReflectionsUtils() {
    }

    public static void initiate(ClassLoader projectClassLoader) {
        ReflectionsUtils.initiate(projectClassLoader, false);
    }

    public static void initiateTestMode() {
        ReflectionsUtils.initiate(ReflectionsUtils.class.getClassLoader(), true);
    }

    private static void initiate(ClassLoader projectClassLoader, boolean testMode) {
        ReflectionsUtils.projectClassLoader = projectClassLoader;
        ReflectionsUtils.testMode = testMode;
        initiated = true;
    }

    public static ClassLoader getProjectClassLoader() {
        if (!initiated) {
            throw new RuntimeException("ReflectionsUtils has not been initiated.");
        }
        return ReflectionsUtils.projectClassLoader;
    }

    public static ConfigurationBuilder createConfigurationBuilder() {
        if (!initiated) {
            throw new RuntimeException("ReflectionsUtils has not been initiated.");
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        if (testMode) {
            configurationBuilder.forPackage("io.github.kbuntrock");
        } else {
            configurationBuilder.addClassLoaders(projectClassLoader)
                    .addUrls(ClasspathHelper.forClassLoader(projectClassLoader));
        }
        return configurationBuilder;
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

    public static String getClassNameFromType(Type type) {
        return type.toString().replaceAll("class ", "").replaceAll("interface ", "");
    }
}
