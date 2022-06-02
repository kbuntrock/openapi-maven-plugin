package com.github.kbuntrock;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.utils.ReflectionsUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class TagLibrary {

    private final List<Tag> tags = new ArrayList<>();
    private final Set<DataObject> schemaObjects = new HashSet<>();

    public void addTag(Tag tag) throws MojoFailureException {
        tags.add(tag);
        try {
            exploreTagObjects(tag);
        } catch (ClassNotFoundException e) {
            throw new MojoFailureException("Class not found for mapping", e);
        }
        System.currentTimeMillis();
    }

    /**
     * Analyse all endpoints of a tag (aka a rest controller) to extract all objects which will be written in the schema section : parameters or response.
     *
     * @param tag a rest controller
     * @throws ClassNotFoundException
     */
    private void exploreTagObjects(Tag tag) throws ClassNotFoundException {
        for (Endpoint endpoint : tag.getEndpoints()) {
            if (endpoint.getResponseObject() != null) {
                exploreDataObject(endpoint.getResponseObject());
            }

            for (ParameterObject parameterObject : endpoint.getParameters()) {
                exploreDataObject(parameterObject);
            }
        }
    }

    private void exploreDataObject(final DataObject dataObject) throws ClassNotFoundException {
        // Generically typed objects are never written in the schema section
        if (dataObject.isReferenceObject() && !dataObject.isGenericallyTyped()) {
            if (schemaObjects.add(dataObject)) {
                inspectObject(dataObject.getJavaClass());
            }
        } else if (dataObject.isGenericallyTyped()) {
            // Eventually analyse instead the generic types
            for (Map.Entry<String, Type> entry : dataObject.getGenericNameToTypeMap().entrySet()) {

                DataObject genericObject = DataObject.create(entry.getValue());
                exploreDataObject(genericObject);
            }
        } else if (dataObject.isJavaArray()) {
            exploreDataObject(dataObject.getArrayItemDataObject());
        }
    }

    private void inspectObject(Class<?> clazz) throws ClassNotFoundException {
        if (clazz.isEnum()) {
            return;
        }
        List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), clazz);
        for (Field field : fields) {
            DataObject dataObject = new DataObject(field.getType(), field.getGenericType());
            exploreDataObject(dataObject);
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public Set<DataObject> getSchemaObjects() {
        return schemaObjects;
    }
}
