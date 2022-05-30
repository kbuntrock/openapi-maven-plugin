package com.github.kbuntrock.yaml.model;

import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.utils.OpenApiConstants;
import com.github.kbuntrock.utils.OpenApiDataFormat;
import com.github.kbuntrock.utils.OpenApiDataType;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
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

        if(isMultipartFile && parameterObject != null) {
            // the MultipartFile must be named in the body.
            Content multipartContent = new Content();
            Schema schema = new Schema();
            if(parameterObject.isRequired()) {
                schema.setRequired(List.of(parameterObject.getName()));
                multipartContent.schema = schema;
            }
            schema.setProperties(content.schema.getProperties());
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

//    public static Content fromDataObjectOld(DataObject dataObject) {
//        if (dataObject == null) {
//            return null;
//        }
//        Content content = new Content();
//
//        if(dataObject.isMap()) {
//            content.getSchema().put(OpenApiConstants.TYPE, dataObject.getOpenApiType().getValue());
//            Map<String, Object> additionalProperties = new LinkedHashMap<>();
//            content.getSchema().put("additionalProperties", additionalProperties);
//            if(dataObject.getMapValueType().isPureObject()) {
//                additionalProperties.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getMapValueType().getJavaClass().getSimpleName());
//            } else {
//                additionalProperties.put(OpenApiConstants.TYPE, dataObject.getMapValueType().getOpenApiType().getValue());
//                OpenApiDataFormat format = dataObject.getMapValueType().getOpenApiType().getFormat();
//                if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
//                    additionalProperties.put("format", format.getValue());
//                }
//
//            }
//        }
//        else if (dataObject.isEnum() || (dataObject.isPureObject() && !dataObject.isGenericallyTyped())) {
//            content.getSchema().put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getJavaClass().getSimpleName());
//        } else if(dataObject.isPureObject() && dataObject.isGenericallyTyped()){
//            content.getSchema().put("toto", new Schema(dataObject));
//        } else {
//            content.getSchema().put(OpenApiConstants.TYPE, dataObject.getOpenApiType().getValue());
//            if (OpenApiDataType.ARRAY != dataObject.getOpenApiType()) {
//                OpenApiDataFormat format = dataObject.getOpenApiType().getFormat();
//                if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
//                    content.getSchema().put("format", format.getValue());
//                }
//            } else if (OpenApiDataType.ARRAY == dataObject.getOpenApiType()) {
//                OpenApiDataType itemType = dataObject.getArrayItemDataObject().getOpenApiType();
//                Map<String, String> itemsMap = new LinkedHashMap<>();
//                if (OpenApiDataFormat.NONE != itemType.getFormat() && OpenApiDataFormat.UNKNOWN != itemType.getFormat()) {
//                    itemsMap.put(OpenApiConstants.TYPE, itemType.getValue());
//                    itemsMap.put("format", itemType.getFormat().getValue());
//                } else if (OpenApiDataType.OBJECT == itemType || dataObject.getArrayItemDataObject().getJavaClass().isEnum()) {
//                    itemsMap.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getArrayItemDataObject().getJavaClass().getSimpleName());
//                }
//                content.getSchema().put("items", itemsMap);
//            }
//        }
//
//        return content;
//    }

    public Schema getSchema() {
        return schema;
    }

}
