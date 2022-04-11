package com.github.kbuntrock;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.utils.OpenApiDataType;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagLibrary {

    private List<Tag> tags = new ArrayList<>();
    private Set<DataObject> schemaObjects = new HashSet<>();

    public TagLibrary() {
    }

    public void addTag(Tag tag) throws MojoFailureException {
        tags.add(tag);
        try {
            mapTagObjects(tag);
        } catch (ClassNotFoundException e) {
            throw new MojoFailureException("Class not found for mapping", e);
        }
        System.out.println();
    }

    private void mapTagObjects(Tag tag) throws ClassNotFoundException {
        for (Endpoint endpoint : tag.getEndpoints()) {
            if(endpoint.getResponseObject()!= null){
                if (OpenApiDataType.OBJECT == endpoint.getResponseObject().getOpenApiType()) {
                    schemaObjects.add(endpoint.getResponseObject());
                    inspectObject(endpoint.getResponseObject().getJavaType());
                } else if(OpenApiDataType.ARRAY == endpoint.getResponseObject().getOpenApiType()) {
                    if(OpenApiDataType.OBJECT == endpoint.getResponseObject().getArrayItemDataObject().getOpenApiType()) {
                        schemaObjects.add(endpoint.getResponseObject().getArrayItemDataObject());
                        inspectObject(endpoint.getResponseObject().getArrayItemDataObject().getJavaType());
                    }
                    // TODO : gérer les nested arrays
                }
            }

            for (ParameterObject parameterObject : endpoint.getParameters()) {
                if (OpenApiDataType.OBJECT == parameterObject.getOpenApiType()) {
                    schemaObjects.add(parameterObject);
                    inspectObject(parameterObject.getJavaType());
                } else if(OpenApiDataType.ARRAY == parameterObject.getOpenApiType()){
                    if(OpenApiDataType.OBJECT == parameterObject.getArrayItemDataObject().getOpenApiType()) {
                        schemaObjects.add(parameterObject.getArrayItemDataObject());
                        inspectObject(parameterObject.getArrayItemDataObject().getJavaType());
                    }
                    // TODO : gérer les nested arrays
                }
            }
        }
    }

    private void inspectObject(Class<?> clazz) throws ClassNotFoundException {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            OpenApiDataType dataType = OpenApiDataType.fromJavaType(fieldType);
            if(OpenApiDataType.OBJECT == dataType) {
                DataObject dataObject = new DataObject();
                dataObject.setJavaType(fieldType, null);
                schemaObjects.add(dataObject);
                inspectObject(dataObject.getJavaType());
            } else if(OpenApiDataType.ARRAY == dataType) {
                DataObject dataObject = new DataObject();
                if(fieldType.isArray()) {
                    dataObject.setJavaType(fieldType, null);
                } else {
                    dataObject.setJavaType(fieldType, ((ParameterizedType) field.getGenericType()));
                }
                if(OpenApiDataType.OBJECT == dataObject.getArrayItemDataObject().getOpenApiType()){
                    schemaObjects.add((dataObject.getArrayItemDataObject()));
                    inspectObject(dataObject.getArrayItemDataObject().getJavaType());
                    // TODO : gérer les nested arrays
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
