package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.OpenApiDataType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represent a type whith all the needed informations to insert it into the openapi specification
 */
public class DataObject {

    /**
     * The original java class
     */
    private Class<?> javaType;
    /**
     * The corresponding openapi type
     */
    private OpenApiDataType openApiType;
    /**
     * The type of the items if this data object represent a java Collection or java array
     */
    private DataObject arrayItemDataObject;
    /**
     * All the value's names if this data object represent a java enum
     */
    private List<String> enumItemValues;

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType, ParameterizedType parameterizedType, ClassLoader projectClassLoader) {
        this.javaType = javaType;
        this.openApiType = OpenApiDataType.fromJavaType(javaType);
        if (javaType.isEnum()) {
            Object[] values = javaType.getEnumConstants();
            this.enumItemValues = new ArrayList<>();
            for (Object value : values) {
                this.enumItemValues.add(value.toString());
            }

        } else if (OpenApiDataType.ARRAY == this.openApiType) {
            arrayItemDataObject = new DataObject();
            if (javaType.isArray()) {
                arrayItemDataObject.setJavaType(javaType.getComponentType(), null, projectClassLoader);
            } else {
                Type listType = parameterizedType.getActualTypeArguments()[0];
                try {
                    Class<?> listTypeClass = Class.forName(listType.getTypeName(), true, projectClassLoader);
                    arrayItemDataObject.setJavaType(listTypeClass, null, projectClassLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found for " + listType.getTypeName());
                }
            }
        }
    }

    public OpenApiDataType getOpenApiType() {
        return openApiType;
    }

    public DataObject getArrayItemDataObject() {
        return arrayItemDataObject;
    }

    public List<String> getEnumItemValues() {
        return enumItemValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataObject that = (DataObject) o;
        return Objects.equals(javaType, that.javaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaType);
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "javaType=" + javaType.getSimpleName() +
                ", openApiType=" + openApiType +
                ", arrayItemDataObject=" + arrayItemDataObject +
                '}';
    }
}
