package io.github.kbuntrock;

import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Keep track of tags and explore them to find every DataObject which should end up in the components/schemas section
 */
public class TagLibrary {

    private final List<Tag> tags = new ArrayList<>();
    private final Set<DataObject> schemaObjects = new HashSet<>();

    public void addTag(Tag tag) throws MojoFailureException {
        tags.add(tag);
        exploreTagObjects(tag);
        System.currentTimeMillis();
    }

    /**
     * Analyse all endpoints of a tag (aka a rest controller) to extract all objects which will be written in the schema section : parameters or response.
     *
     * @param tag a rest controller
     */
    private void exploreTagObjects(Tag tag) {
        for (Endpoint endpoint : tag.getEndpoints()) {
            if (endpoint.getResponseObject() != null) {
                exploreDataObject(endpoint.getResponseObject());
            }

            for (ParameterObject parameterObject : endpoint.getParameters()) {
                exploreDataObject(parameterObject);
            }
        }
    }

    private void exploreDataObject(final DataObject dataObject) {
        // Generically typed objects are never written in the schema section
        if (dataObject.isReferenceObject() && !dataObject.isGenericallyTyped()) {
            if (schemaObjects.add(dataObject)) {
                inspectObject(dataObject.getJavaClass());
            }
        } else if (dataObject.isGenericallyTyped()) {
            // Eventually analyse instead the generic types
            for (Map.Entry<String, Type> entry : dataObject.getGenericNameToTypeMap().entrySet()) {

                DataObject genericObject = new DataObject(entry.getValue());
                exploreDataObject(genericObject);
            }
        } else if (dataObject.isJavaArray()) {
            exploreDataObject(dataObject.getArrayItemDataObject());
        }
    }

    private void inspectObject(Class<?> clazz) {
        if (clazz.isEnum()) {
            return;
        }
        List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), clazz);
        for (Field field : fields) {
            DataObject dataObject = new DataObject(field.getGenericType());
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
