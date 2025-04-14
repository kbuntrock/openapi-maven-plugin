package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Content {
	private Schema schema;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String, ContentType> encoding;

	public static Content fromMultipartBodies(final List<ParameterObject> parameterObjects){
		final Content content = new Content();

		content.schema = new Schema();
		content.schema.setType(OpenApiTypeResolver.OBJECT_TYPE);
		content.schema.setRequired(
			parameterObjects.stream()
				.filter(ParameterObject::isRequired)
				.map(ParameterObject::getName)
				.collect(Collectors.toList()));

		content.schema.properties = parameterObjects.stream()
			.collect(Collectors.toMap(
				ParameterObject::getName,
				po-> new Property(fromDataObject(po).schema)));

		return content;
	}

	public static Content fromMultipartFormData(final List<ParameterObject> bodyParts, final JavadocWrapper methodJavadoc){
		final Content content = new Content();

		content.schema = new Schema();
		content.schema.setType(OpenApiTypeResolver.OBJECT_TYPE);
		content.schema.setRequired(
			bodyParts.stream()
				.filter(ParameterObject::isRequired)
				.map(ParameterObject::getName)
				.collect(Collectors.toList()));

		content.encoding = new LinkedHashMap<>();
		content.schema.properties = new LinkedHashMap<>();
		for(ParameterObject bodyPart : bodyParts) {
			Property property = new Property(fromDataObject(bodyPart).schema);
			content.schema.properties.put(bodyPart.getName(), property);

			if(bodyPart.getOpenApiResolvedType().getDefaultEncoding() != null) {
				content.encoding.put(bodyPart.getName(), new ContentType(bodyPart.getOpenApiResolvedType().getDefaultEncoding()));
			}

			// Javadoc handling
			if(methodJavadoc != null) {
				final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(bodyPart.getJavadocFieldName());
				if(parameterDoc.isPresent()) {
					final String description = parameterDoc.get().getContent().toText();
					if(!description.isEmpty()) {
						property.setDescription(parameterDoc.get().getContent().toText());
					}
				}
			}
		}

		return content;
	}

	public static Content fromDataObject(final DataObject dataObject) {
		if(dataObject == null) {
			return null;
		}
		final Set<String> exploredSignatures = new HashSet<>();
		final Content content = new Content();
		content.schema = new Schema(dataObject, exploredSignatures);
		return content;
	}

	public Schema getSchema() {
		return schema;
	}

	public Map<String, ContentType> getEncoding() {
		return encoding;
	}
}
