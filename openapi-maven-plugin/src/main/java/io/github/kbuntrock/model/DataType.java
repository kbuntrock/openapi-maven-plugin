package io.github.kbuntrock.model;

import io.github.kbuntrock.utils.OpenApiDataType;

public class DataType {

    private OpenApiDataType openApiDataType;
    private String format;
    /**
     * In case the main datatype is an array
     */
    private DataType itemsType;
}
