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
            if (endpoint.getResponseObject() != null) {
                DataObject responseObject = endpoint.getResponseObject();
                // Generically typed objects are never written in the schema section
                if (responseObject.isPureObject() && !responseObject.isGenericallyTyped()) {
                    if (schemaObjects.add(responseObject)) {
                        inspectObject(responseObject.getJavaClass());
                    }
                } else if (responseObject.isMap()) {
                    if (responseObject.getMapKeyType().isPureObject() && !responseObject.getMapKeyType().isGenericallyTyped()
                            && schemaObjects.add(responseObject.getMapKeyType())) {
                        inspectObject(responseObject.getMapKeyType().getJavaClass());
                    }
                    if (responseObject.getMapValueType().isPureObject() && !responseObject.getMapValueType().isGenericallyTyped()
                            && schemaObjects.add(responseObject.getMapValueType())) {
                        inspectObject(responseObject.getMapValueType().getJavaClass());
                    }
                } else if (OpenApiDataType.ARRAY == responseObject.getOpenApiType()) {
                    if (responseObject.getArrayItemDataObject().isPureObject() && !responseObject.getArrayItemDataObject().isGenericallyTyped()) {
                        if (schemaObjects.add(responseObject.getArrayItemDataObject())) {
                            inspectObject(responseObject.getArrayItemDataObject().getJavaClass());
                        }
                    }
                }
            }

            for (ParameterObject parameterObject : endpoint.getParameters()) {
                if (parameterObject.isPureObject() && !parameterObject.isGenericallyTyped()) {
                    if (schemaObjects.add(parameterObject)) {
                        inspectObject(parameterObject.getJavaClass());
                    }
                } else if (parameterObject.isMap()) {
                    if (parameterObject.getMapKeyType().isPureObject() && !parameterObject.getMapKeyType().isGenericallyTyped()
                            && schemaObjects.add(parameterObject.getMapKeyType())) {
                        inspectObject(parameterObject.getMapKeyType().getJavaClass());
                    }
                    if (parameterObject.getMapValueType().isPureObject() && !parameterObject.getMapValueType().isGenericallyTyped()
                            && schemaObjects.add(parameterObject.getMapValueType())) {
                        inspectObject(parameterObject.getMapValueType().getJavaClass());
                    }
                } else if (OpenApiDataType.ARRAY == parameterObject.getOpenApiType()) {
                    if (parameterObject.getArrayItemDataObject().isPureObject() && !parameterObject.getArrayItemDataObject().isGenericallyTyped()) {
                        if (schemaObjects.add(parameterObject.getArrayItemDataObject())) {
                            inspectObject(parameterObject.getArrayItemDataObject().getJavaClass());
                        }
                    }
                }
            }
        }
    }

    private void inspectObject(Class<?> clazz) {
        if (clazz.isEnum()) {
            return;
        }
        List<Field> fields = ReflexionUtils.getAllNonStaticFields(new ArrayList<>(), clazz);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            OpenApiDataType dataType = OpenApiDataType.fromJavaClass(fieldType);
            if (field.getType().isAssignableFrom(Map.class)) {
                DataObject dataObject = new DataObject(fieldType, ((ParameterizedType) field.getGenericType()));
                if (dataObject.getMapValueType().isEnum() || OpenApiDataType.OBJECT == dataObject.getMapValueType().getOpenApiType()) {
                    if (schemaObjects.add((dataObject.getMapValueType()))) {
                        inspectObject(dataObject.getMapValueType().getJavaClass());
                    }
                }

            } else if (OpenApiDataType.OBJECT == dataType || fieldType.isEnum()) {
                DataObject dataObject = new DataObject(fieldType, null);
                if (schemaObjects.add(dataObject)) {
                    // Inspect the object if it is not already known
                    inspectObject(dataObject.getJavaClass());
                }

            } else if (OpenApiDataType.ARRAY == dataType) {
                DataObject dataObject;
                if (fieldType.isArray()) {
                    dataObject = new DataObject(fieldType, null);
                } else {
                    dataObject = new DataObject(fieldType, ((ParameterizedType) field.getGenericType()));
                }
                if (dataObject.getArrayItemDataObject().getJavaClass().isEnum()
                                || OpenApiDataType.OBJECT == dataObject.getArrayItemDataObject().getOpenApiType()) {
                    if (schemaObjects.add((dataObject.getArrayItemDataObject()))) {
                        inspectObject(dataObject.getArrayItemDataObject().getJavaClass());
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
