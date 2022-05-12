package com.github.kbuntrock.yaml.model;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.utils.OpenApiDataFormat;
import com.github.kbuntrock.utils.OpenApiDataType;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Content {

    private final Map<String, Object> schema = new LinkedHashMap<>();

    public Map<String, Object> getSchema() {
        return schema;
    }

    public static Content fromDataObject(ParameterObject parameterObject) {

        Content content = fromDataObject((DataObject) parameterObject);

        boolean isMultipartFile = MultipartFile.class == parameterObject.getJavaType() ||
                (OpenApiDataType.ARRAY == parameterObject.getOpenApiType() && MultipartFile.class == parameterObject.getArrayItemDataObject().getJavaType());
        if(isMultipartFile && parameterObject != null) {
            // the MultipartFile must be named in the body.
            Content multipartContent = new Content();
            if(parameterObject.isRequired()) {
                multipartContent.getSchema().put("required", List.of(parameterObject.getName()));
            }
            Map<String, Object> paramContent = new LinkedHashMap<>();
            multipartContent.getSchema().put("properties", paramContent);
            paramContent.put(parameterObject.getName(), content.getSchema());
            return multipartContent;
        }
        return content;
    }

    public static Content fromDataObject(DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }
        Content content = new Content();

        if(dataObject.isMap()) {
            content.getSchema().put("type", dataObject.getOpenApiType().getValue());
            Map<String, Object> additionalProperties = new LinkedHashMap<>();
            content.getSchema().put("additionalProperties", additionalProperties);
            if(dataObject.getMapValueType().isPureObject()) {
                additionalProperties.put("$ref", "#/components/schemas/" + dataObject.getMapValueType().getJavaType().getSimpleName());
            } else {
                additionalProperties.put("type", dataObject.getMapValueType().getOpenApiType().getValue());
                OpenApiDataFormat format = dataObject.getMapValueType().getOpenApiType().getFormat();
                if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                    additionalProperties.put("format", format.getValue());
                }

            }
        }
        else if (OpenApiDataType.OBJECT == dataObject.getOpenApiType() || dataObject.isEnum()) {
            content.getSchema().put("$ref", "#/components/schemas/" + dataObject.getJavaType().getSimpleName());
        } else {
            content.getSchema().put("type", dataObject.getOpenApiType().getValue());
            if (OpenApiDataType.ARRAY != dataObject.getOpenApiType()) {
                OpenApiDataFormat format = dataObject.getOpenApiType().getFormat();
                if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                    content.getSchema().put("format", format.getValue());
                }
            } else if (OpenApiDataType.ARRAY == dataObject.getOpenApiType()) {
                OpenApiDataType itemType = dataObject.getArrayItemDataObject().getOpenApiType();
                Map<String, String> itemsMap = new LinkedHashMap<>();
                if (OpenApiDataFormat.NONE != itemType.getFormat() && OpenApiDataFormat.UNKNOWN != itemType.getFormat()) {
                    itemsMap.put("type", itemType.getValue());
                    itemsMap.put("format", itemType.getFormat().getValue());
                } else if (OpenApiDataType.OBJECT == itemType || dataObject.getArrayItemDataObject().getJavaType().isEnum()) {
                    itemsMap.put("$ref", "#/components/schemas/" + dataObject.getArrayItemDataObject().getJavaType().getSimpleName());
                }
                content.getSchema().put("items", itemsMap);
            }
        }

        return content;
    }
}
