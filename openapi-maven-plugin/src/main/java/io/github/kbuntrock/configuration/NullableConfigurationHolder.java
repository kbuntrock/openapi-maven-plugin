package io.github.kbuntrock.configuration;

public class NullableConfigurationHolder {

    private static String nullableAnnotation;

    private static String nonNullAnnotation;

    private static boolean defaultNonNullableFields;

    public static void storeConfig(final CommonApiConfiguration commonApiConfiguration) {
        nullableAnnotation = commonApiConfiguration.nullableAnnotation;
        nonNullAnnotation = commonApiConfiguration.nonNullableAnnotation;
        defaultNonNullableFields = commonApiConfiguration.defaultNonNullableFields != null
                && commonApiConfiguration.defaultNonNullableFields;

    }

    public static String getNullableAnnotation() {
        return nullableAnnotation;
    }

    public static String getNonNullAnnotation() {
        return nonNullAnnotation;
    }

    public static boolean isDefaultNonNullableFields() {
        return defaultNonNullableFields;
    }
}
