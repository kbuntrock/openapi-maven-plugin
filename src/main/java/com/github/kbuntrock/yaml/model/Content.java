package com.github.kbuntrock.yaml.model;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.utils.OpenApiDataFormat;
import com.github.kbuntrock.utils.OpenApiDataType;

import java.util.LinkedHashMap;
import java.util.Map;

public class Content {

    private Map<String, Object> schema = new LinkedHashMap<>();

    public Map<String, Object> getSchema() {
        return schema;
    }

    public static Content fromDataObject(DataObject dataObject) {
        if (dataObject == null) {
            return null;
        }
        Content content = new Content();

        if (OpenApiDataType.OBJECT == dataObject.getOpenApiType()) {
            content.getSchema().put("$ref", "#/components/schemas/" + dataObject.getJavaType().getSimpleName());
        } else {
            content.getSchema().put("type", dataObject.getOpenApiType().getValue());
            if(OpenApiDataType.ARRAY != dataObject.getOpenApiType()){
                OpenApiDataFormat format = dataObject.getOpenApiType().getFormat();
                if(OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format){
                    content.getSchema().put("format", format.getValue());
                }
            } else if(OpenApiDataType.ARRAY == dataObject.getOpenApiType()) {
                OpenApiDataType itemType = dataObject.getArrayItemDataObject().getOpenApiType();
                Map<String, String> itemsMap = new LinkedHashMap<>();
                itemsMap.put("type", itemType.getValue());
                if(OpenApiDataFormat.NONE != itemType.getFormat() && OpenApiDataFormat.UNKNOWN != itemType.getFormat()){
                    itemsMap.put("format", itemType.getFormat().getValue());
                } else if (OpenApiDataType.OBJECT == itemType) {
                    itemsMap.put("$ref", "#/components/schemas/" + dataObject.getArrayItemDataObject().getJavaType().getSimpleName());
                }
                content.getSchema().put("items", itemsMap);
            }
        }

        return content;
    }
}
