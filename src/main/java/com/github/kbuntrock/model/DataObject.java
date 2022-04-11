package com.github.kbuntrock.model;

import com.github.kbuntrock.utils.OpenApiDataType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class DataObject {

    private Class javaType;
    private OpenApiDataType openApiType;
    private DataObject arrayItemDataObject;

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType, ParameterizedType parameterizedType) {
        this.javaType = javaType;
        this.openApiType = OpenApiDataType.fromJavaType(javaType);
        if(OpenApiDataType.ARRAY == this.openApiType){
            arrayItemDataObject = new DataObject();
            if(javaType.isArray()) {
                arrayItemDataObject.setJavaType(javaType.getComponentType(), null);
            } else {
                Type listType = parameterizedType.getActualTypeArguments()[0];
                try {
                    Class<?> listTypeClass = Class.forName(listType.getTypeName());
                    arrayItemDataObject.setJavaType(listTypeClass, null);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found for "+listType.getTypeName());
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
}
