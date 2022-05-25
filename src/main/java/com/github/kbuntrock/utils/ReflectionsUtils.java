package com.github.kbuntrock.utils;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

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
        if(!initiated) {
            throw new RuntimeException("ReflectionsUtils has not been initiated.");
        }
        return ReflectionsUtils.projectClassLoader;
    }

    public static ConfigurationBuilder getConfigurationBuilder() {
        if(!initiated) {
            throw new RuntimeException("ReflectionsUtils has not been initiated.");
        }
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        if(testMode) {
            configurationBuilder.forPackage("com.github.kbuntrock");
        } else {
            configurationBuilder.addClassLoaders(projectClassLoader)
                    .addUrls(ClasspathHelper.forClassLoader(projectClassLoader));
        }
        return configurationBuilder;
    }
}
