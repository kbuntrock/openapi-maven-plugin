package io.github.kbuntrock.yaml.model;

import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.utils.OpenApiDataType;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class Content {

    private Schema schema;

    public static Content fromDataObject(ParameterObject parameterObject) {

        Content content = fromDataObject((DataObject) parameterObject);

        boolean isMultipartFile = MultipartFile.class == parameterObject.getJavaClass() ||
                (OpenApiDataType.ARRAY == parameterObject.getOpenApiType() && MultipartFile.class == parameterObject.getArrayItemDataObject().getJavaClass());
        
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
        Set<String> exploredSignatures = new HashSet<>();
        Content content = new Content();
        content.schema = new Schema(dataObject, exploredSignatures);
        return content;
    }

    public Schema getSchema() {
        return schema;
    }

}
