package com.github.kbuntrock.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.kbuntrock.TagLibrary;
import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.utils.OpenApiDataType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
        // TreeMap to keep alphabetical order
        Map<String, ComponentSchema> schemas = new TreeMap<>();
        component.setSchemas(schemas);
        for (DataObject dataObject : ordered) {
            ComponentSchema schema = new ComponentSchema();
            component.getSchemas().put(dataObject.getJavaType().getSimpleName(), schema);
            schema.setType(dataObject.getOpenApiType().getApiName());
            // Tree map to keep it ordered
            Map<String, Property> properties = new HashMap<>();
            schema.setProperties(properties);
            for (Field field : dataObject.getJavaType().getDeclaredFields()) {
                Property property = new Property();
                property.setName(field.getName());
                OpenApiDataType openApiDataType = OpenApiDataType.fromJavaType(field.getType());
                property.setType(openApiDataType.getApiName());
                if(OpenApiDataType.ARRAY == openApiDataType){
                    property.setUniqueItems(true);
                   // property.setItems(new Items());
                }
                properties.put(property.getName(), property);
            }
        }
        return component;
    }
}

