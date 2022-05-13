package com.github.kbuntrock;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.utils.OpenApiDataType;
import com.github.kbuntrock.utils.ReflexionUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class TagLibrary {

    private final List<Tag> tags = new ArrayList<>();
    private final Set<DataObject> schemaObjects = new HashSet<>();

    private final ClassLoader projectClassLoader;

    public TagLibrary(ClassLoader projectClassLoader) {
        this.projectClassLoader = projectClassLoader;
    }

    public void addTag(Tag tag) throws MojoFailureException {
        tags.add(tag);
        try {
            mapTagObjects(tag);
        } catch (ClassNotFoundException e) {
            throw new MojoFailureException("Class not found for mapping", e);
        }
    }

    private void mapTagObjects(Tag tag) throws ClassNotFoundException {
        for (Endpoint endpoint : tag.getEndpoints()) {
            if(endpoint.getResponseObject() != null){
                if (endpoint.getResponseObject().isPureObject()) {
                    if(schemaObjects.add(endpoint.getResponseObject())) {
                        inspectObject(endpoint.getResponseObject().getJavaType());
                    }
                } else if(endpoint.getResponseObject().isMap()) {
                    if(endpoint.getResponseObject().getMapKeyType().isPureObject() && schemaObjects.add(endpoint.getResponseObject().getMapKeyType())) {
                        inspectObject(endpoint.getResponseObject().getMapKeyType().getJavaType());
                    }
                    if(endpoint.getResponseObject().getMapValueType().isPureObject() && schemaObjects.add(endpoint.getResponseObject().getMapValueType())) {
                        inspectObject(endpoint.getResponseObject().getMapValueType().getJavaType());
                    }
                } else if(OpenApiDataType.ARRAY == endpoint.getResponseObject().getOpenApiType()) {
                    if(endpoint.getResponseObject().getArrayItemDataObject().isPureObject()) {
                        if(schemaObjects.add(endpoint.getResponseObject().getArrayItemDataObject())) {
                            inspectObject(endpoint.getResponseObject().getArrayItemDataObject().getJavaType());
                        }
                    }
                    // TODO : Handle nested arrays
                }
            }

            for (ParameterObject parameterObject : endpoint.getParameters()) {
                if (parameterObject.isPureObject()) {
                    if(schemaObjects.add(parameterObject)) {
                        inspectObject(parameterObject.getJavaType());
                    }
                } else if(parameterObject.isMap()) {
                    if(parameterObject.getMapKeyType().isPureObject() && schemaObjects.add(parameterObject.getMapKeyType())) {
                        inspectObject(parameterObject.getMapKeyType().getJavaType());
                    }
                    if(parameterObject.getMapValueType().isPureObject() && schemaObjects.add(parameterObject.getMapValueType())) {
                        inspectObject(parameterObject.getMapValueType().getJavaType());
                    }
                } else if(OpenApiDataType.ARRAY == parameterObject.getOpenApiType()){
                    if(parameterObject.getArrayItemDataObject().isPureObject()) {
                        if(schemaObjects.add(parameterObject.getArrayItemDataObject())) {
                            inspectObject(parameterObject.getArrayItemDataObject().getJavaType());
                        }
                    }
                    // TODO : Handle nested arrays
                }
            }
        }
    }

    private void inspectObject(Class<?> clazz) {
        if(clazz.isEnum()) {
           return;
        }
        List<Field> fields = ReflexionUtils.getAllNonStaticFields(new ArrayList<>(), clazz);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            OpenApiDataType dataType = OpenApiDataType.fromJavaType(fieldType);
            if(field.getType().isAssignableFrom(Map.class)) {
                DataObject dataObject = new DataObject();
                dataObject.setJavaType(fieldType, ((ParameterizedType) field.getGenericType()), projectClassLoader);
                if(dataObject.getMapValueType().isEnum() || OpenApiDataType.OBJECT == dataObject.getMapValueType().getOpenApiType()){
                    if(schemaObjects.add((dataObject.getMapValueType()))) {
                        inspectObject(dataObject.getMapValueType().getJavaType());
                    }
                }

            } else if(OpenApiDataType.OBJECT == dataType || fieldType.isEnum()) {
                DataObject dataObject = new DataObject();
                dataObject.setJavaType(fieldType, null, projectClassLoader);
                if(schemaObjects.add(dataObject)){
                    // Inspect the object if it is not already known
                    inspectObject(dataObject.getJavaType());
                }

            } else if(OpenApiDataType.ARRAY == dataType) {
                DataObject dataObject = new DataObject();
                if(fieldType.isArray()) {
                    dataObject.setJavaType(fieldType, null, projectClassLoader);
                } else {
                    dataObject.setJavaType(fieldType, ((ParameterizedType) field.getGenericType()), projectClassLoader);
                }
                if(dataObject.getArrayItemDataObject().getJavaType().isEnum() || OpenApiDataType.OBJECT == dataObject.getArrayItemDataObject().getOpenApiType()){
                    if(schemaObjects.add((dataObject.getArrayItemDataObject()))) {
                        inspectObject(dataObject.getArrayItemDataObject().getJavaType());
                    }
                    // TODO : Handle nested arrays
                }
            }
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Set<DataObject> getSchemaObjects() {
        return schemaObjects;
    }
}
