package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.OpenApiDataType;
import com.github.kbuntrock.utils.ReflectionsUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Represent a type whith all the needed informations to insert it into the openapi specification
 */
public class DataObject {

    /**
     * The original java class
     */
    private Class<?> javaClass;
    /**
     * The original java type
     */
    private Type javaType;
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
    /**
     * True if this object is generically typed
     */
    private boolean genericallyTyped;
    /**
     * Map the generic name of a type to the class of the use case.
     */
    private Map<String, Class<?>> genericNameToClassMap;

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public Type getJavaType() {
        return javaType;
    }

    /**
     * @return true if this DataObject is a map
     */
    public boolean isMap() {
        return mapKeyValueDataObjects[0] != null;
    }

    /**
     * @return true if this DataObject is an enum
     */
    public boolean isEnum() {
        return javaClass.isEnum();
    }

    /**
     * @return true if the object should be considered as a "reference object", in order to get its own schema section
     */
    public boolean isPureObject() {
        return (OpenApiDataType.OBJECT == openApiType && !isMap()) || isEnum();
    }

    /**
     * @return true if the object is an array
     */
    public boolean isArray() {
        return OpenApiDataType.ARRAY == openApiType;
    }

    public DataObject(Class<?> javaClass, Type javaType) {
        this.javaType = javaType;
        if (javaType instanceof ParameterizedType) {
            initialize(javaClass, (ParameterizedType) javaType);
        } else {
            initialize(javaClass, null);
        }
    }

    public DataObject(Class<?> javaClass, ParameterizedType parameterizedType) {
        initialize(javaClass, parameterizedType);
    }

    private void initialize(Class<?> javaClass, ParameterizedType parameterizedType) {

        try {
            this.javaClass = javaClass;
            this.openApiType = OpenApiDataType.fromJavaClass(javaClass);
            this.genericallyTyped = parameterizedType != null;
            if (this.genericallyTyped) {
                this.genericNameToClassMap = new HashMap<>();
                for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                    this.genericNameToClassMap.put(this.javaClass.getTypeParameters()[i].getTypeName(),
                            Class.forName(parameterizedType.getActualTypeArguments()[i].getTypeName(), true, ReflectionsUtils.getProjectClassLoader()));
                }
            }
            if (javaClass.isEnum()) {
                Object[] values = javaClass.getEnumConstants();
                this.enumItemValues = new ArrayList<>();
                for (Object value : values) {
                    this.enumItemValues.add(value.toString());
                }

            } else if (OpenApiDataType.ARRAY == this.openApiType) {

                if (javaClass.isArray()) {
                    arrayItemDataObject = new DataObject(javaClass.getComponentType(), null);
                } else {
                    Type listType = parameterizedType.getActualTypeArguments()[0];
                    Class<?> listTypeClass = Class.forName(listType.getTypeName(), true, ReflectionsUtils.getProjectClassLoader());
                    arrayItemDataObject = new DataObject(listTypeClass, null);
                }
            } else if (javaClass != Object.class && javaClass.isAssignableFrom(Map.class)) {
                Class<?> keyTypeClass = Class.forName(parameterizedType.getActualTypeArguments()[0].getTypeName(), true, ReflectionsUtils.getProjectClassLoader());
                Class<?> valueTypeClass = Class.forName(parameterizedType.getActualTypeArguments()[1].getTypeName(), true, ReflectionsUtils.getProjectClassLoader());

                DataObject key = new DataObject(keyTypeClass, null);
                DataObject value = new DataObject(valueTypeClass, null);

                mapKeyValueDataObjects[0] = key;
                mapKeyValueDataObjects[1] = value;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found in DataObject instanciation", e);
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

    public boolean isGenericallyTyped() {
        return genericallyTyped;
    }

    public Map<String, Class<?>> getGenericNameToClassMap() {
        return genericNameToClassMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataObject that = (DataObject) o;
        return Objects.equals(javaClass, that.javaClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaClass);
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "openApiType=" + openApiType +
                ", arrayItemDataObject=" + arrayItemDataObject +
                '}';
    }
}
