package com.github.kbuntrock.yaml.model;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.utils.OpenApiDataType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Content {

//    private final Map<String, Object> schema = new LinkedHashMap<>();
//
//    public Map<String, Object> getSchema() {
//        return schema;
//    }

    private Schema schema;

    public static Content fromDataObject(ParameterObject parameterObject) {

        Content content = fromDataObject((DataObject) parameterObject);

        boolean isMultipartFile = MultipartFile.class == parameterObject.getJavaClass() ||
                (OpenApiDataType.ARRAY == parameterObject.getOpenApiType() && MultipartFile.class == parameterObject.getArrayItemDataObject().getJavaClass());
        boolean isArray = OpenApiDataType.ARRAY == parameterObject.getOpenApiType();
        if (isMultipartFile) {
            // the MultipartFile must be named in the body.
            Content multipartContent = new Content();
            Schema schema = new Schema();
            multipartContent.schema = schema;
            if (parameterObject.isRequired()) {
                schema.setRequired(Arrays.asList(parameterObject.getName()));
            }
            schema.setType(OpenApiDataType.OBJECT.getValue());
            Map<String, Property> propertyMap = new LinkedHashMap<>();
            schema.setProperties(propertyMap);
            Property property = new Property(content.getSchema());
            if (OpenApiDataType.ARRAY.getValue().equals(property.getType())) {
                property.setUniqueItems(true);
            }
            propertyMap.put(parameterObject.getName(), property);
            return multipartContent;
        }
        return content;
    }

    public static Content fromDataObject(DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }
        Content content = new Content();
        content.schema = new Schema(dataObject);
        return content;
    }

    public Schema getSchema() {
        return schema;
    }

}
