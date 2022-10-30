package io.github.kbuntrock.yaml.model;

import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.utils.OpenApiDataType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public class Content {

	private Schema schema;

	public static Content fromDataObject(ParameterObject parameterObject) {

		Content content = fromDataObject((DataObject) parameterObject);

		boolean isMultipartFile = MultipartFile.class == parameterObject.getJavaClass() ||
			(OpenApiDataType.ARRAY == parameterObject.getOpenApiType() && MultipartFile.class == parameterObject.getArrayItemDataObject()
				.getJavaClass());

		if(isMultipartFile) {
			// the MultipartFile must be named in the body.
			Content multipartContent = new Content();
			Schema schema = new Schema();
			multipartContent.schema = schema;
			if(parameterObject.isRequired()) {
				schema.setRequired(Collections.singletonList(parameterObject.getName()));
			}
			schema.setType(OpenApiDataType.OBJECT.getValue());
			Map<String, Property> propertyMap = new LinkedHashMap<>();
			schema.setProperties(propertyMap);
			Property property = new Property(content.getSchema());
			if(OpenApiDataType.ARRAY.getValue().equals(property.getType())) {
				property.setUniqueItems(true);
			}
			propertyMap.put(parameterObject.getName(), property);
			return multipartContent;
		}
		return content;
	}

	public static Content fromDataObject(DataObject dataObject) {
		if(dataObject == null) {
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
