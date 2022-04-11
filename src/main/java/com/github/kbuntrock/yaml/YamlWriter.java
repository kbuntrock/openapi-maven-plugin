package com.github.kbuntrock.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kbuntrock.TagLibrary;
import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.utils.OpenApiDataFormat;
import com.github.kbuntrock.utils.OpenApiDataType;

import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public enum YamlWriter {
    INSTANCE;

    private final ObjectMapper om;

    private YamlWriter() {
        om = new ObjectMapper(new YAMLFactory());
    }

    public void write(File file, TagLibrary tagLibrary) throws IOException {
        Component component = createComponent(tagLibrary);
        om.writeValue(file, component);
    }

    private Component createComponent(TagLibrary library) {
        List<DataObject> ordered = library.getSchemaObjects().stream()
                .sorted(Comparator.comparing(p -> p.getJavaType().getSimpleName())).collect(Collectors.toList());

        Component component = new Component();
        // LinkedHashMap to keep alphabetical order
        Map<String, ComponentSchema> schemas = new LinkedHashMap<>();
        component.setSchemas(schemas);
        for (DataObject dataObject : ordered) {
            ComponentSchema schema = new ComponentSchema();
            component.getSchemas().put(dataObject.getJavaType().getSimpleName(), schema);
            schema.setType(dataObject.getOpenApiType().getValue());
            // LinkedHashMap to keep the order of the class
            Map<String, Property> properties = new LinkedHashMap<>();
            schema.setProperties(properties);

            for (Field field : getAllFields(new ArrayList<>(), dataObject.getJavaType())) {
                Property property = new Property();
                property.setName(field.getName());
                OpenApiDataType openApiDataType = OpenApiDataType.fromJavaType(field.getType());
                property.setType(openApiDataType.getValue());
                OpenApiDataFormat format = openApiDataType.getFormat();
                if(OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format){
                    property.setFormat(format.getValue());
                }

                if(OpenApiDataType.ARRAY == openApiDataType){
                   extractArrayType(field, property);
                }
                extractConstraints(field, property);
                properties.put(property.getName(), property);
            }
        }
        return component;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return fields;
    }

    private void extractArrayType(Field field, Property property) {
        property.setUniqueItems(true);
        DataObject item = new DataObject();
        if(field.getType().isArray()) {
            item.setJavaType(field.getType(), null);
        } else {
            item.setJavaType(field.getType(), ((ParameterizedType) field.getGenericType()));
        }
        Map<String, String> items = new LinkedHashMap<>();
        items.put("type", item.getArrayItemDataObject().getOpenApiType().getValue());
        property.setItems(items);
    }

    private void extractConstraints(Field field, Property property) {
        Size size = field.getAnnotation(Size.class);
        if(size != null) {
            property.setMinLength(size.min());
            if(size.max() != Integer.MAX_VALUE) {
                property.setMaxLength(size.max());
            }
        }
    }
}

