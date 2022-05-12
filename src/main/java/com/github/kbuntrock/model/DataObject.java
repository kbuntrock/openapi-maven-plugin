package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.OpenApiDataType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    /**
     * Array of two elements in case of a map object :
     * index 0 : the key type
     * index 1 : the value type
     */
    private final DataObject[] mapKeyValueDataObjects = new DataObject[2];

    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     *
     * @return true if this DataObject is a map
     */
    public boolean isMap() {
        return mapKeyValueDataObjects[0] != null;
    }

    /**
     *
     * @return true if this DataObject is an enum
     */
    public boolean isEnum() {
        return javaType.isEnum();
    }

    /**
     *
     * @return true if the object should be considered as a "reference object", in order to get its own schema section
     */
    public boolean isPureObject() {
        return (OpenApiDataType.OBJECT == openApiType && !isMap()) || isEnum();
    }

    /**
     *
     * @return true if the object is an array
     */
    public boolean isArray() {
        return OpenApiDataType.ARRAY == openApiType;
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
        } else if (javaType.isAssignableFrom(Map.class)) {
            DataObject key = new DataObject();
            DataObject value = new DataObject();
            Class<?> keyTypeClass = null;
            try {
                keyTypeClass = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName(), true, projectClassLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found for map key : " + parameterizedType.getActualTypeArguments()[0].getTypeName());
            }
            Class<?> valueTypeClass = null;
            try {
                valueTypeClass = Class.forName(parameterizedType.getActualTypeArguments()[1].getTypeName(), true, projectClassLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found for map value " + parameterizedType.getActualTypeArguments()[1].getTypeName());
            }
            key.setJavaType(keyTypeClass, null, projectClassLoader);
            value.setJavaType(valueTypeClass, null, projectClassLoader);

            mapKeyValueDataObjects[0] = key;
            mapKeyValueDataObjects[1] = value;
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

    public DataObject getMapKeyType() {
        return mapKeyValueDataObjects[0];
    }

    public DataObject getMapValueType() {
        return mapKeyValueDataObjects[1];
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
