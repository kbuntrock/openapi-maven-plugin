package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Content {

	@JsonIgnore
	private List<Schema> schemas;

	@JsonIgnore
	private Map<String, ContentType> encoding;

	public static Content fromMultipartBodies(final List<ParameterObject> parameterObjects, final TagLibrary tagLibrary){
		final Content content = new Content();

		final Schema schema = new Schema(tagLibrary.getApiConfiguration());
		content.schemas = new ArrayList<>();
		content.schemas.add(schema);

		schema.setType(OpenApiTypeResolver.OBJECT_TYPE);
		schema.setRequired(
			parameterObjects.stream()
				.filter(ParameterObject::isRequired)
				.map(ParameterObject::getName)
				.collect(Collectors.toList()));

		schema.properties = parameterObjects.stream()
			.collect(Collectors.toMap(
				ParameterObject::getName,
				po-> new Property(fromDataObject(po, tagLibrary).getSingleSchema())));

		return content;
	}

	public static Content fromMultipartFormData(final List<ParameterObject> bodyParts,
												final JavadocWrapper methodJavadoc, final TagLibrary tagLibrary){
		final Content content = new Content();

		final Schema schema = new Schema(tagLibrary.getApiConfiguration());
		content.schemas = new ArrayList<>();
		content.schemas.add(schema);

		schema.setType(OpenApiTypeResolver.OBJECT_TYPE);
		schema.setRequired(
			bodyParts.stream()
				.filter(ParameterObject::isRequired)
				.map(ParameterObject::getName)
				.collect(Collectors.toList()));

		content.encoding = new LinkedHashMap<>();
		schema.properties = new LinkedHashMap<>();
		for(ParameterObject bodyPart : bodyParts) {
			Property property = new Property(fromDataObject(bodyPart, tagLibrary).getSingleSchema());
			schema.properties.put(bodyPart.getName(), property);

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

	public static Content fromDataObject(final DataObject dataObject, final TagLibrary tagLibrary) {
		if(dataObject == null) {
			return null;
		}
		final Set<String> exploredSignatures = new HashSet<>();
		final Content content = new Content();
		content.schemas = new ArrayList<>();
		content.schemas.add(new Schema(dataObject, exploredSignatures, tagLibrary));
		return content;
	}

	@JsonIgnore
	public Schema getSingleSchema() {
		return schemas == null ? null : schemas.get(0);
	}

	@JsonIgnore
	public List<Schema> getSchemaList() {
		return schemas;
	}

	@JsonIgnore
	public Map<String, ContentType> getEncoding() {
		return encoding;
	}

	@JsonAnyGetter
	public Map<String, Object> getJsonObject() {
		final Map<String, Object> contentMap = new LinkedHashMap<>();
		if(schemas != null) {
			if(schemas.size() == 1) {
				contentMap.put("schema", schemas.get(0));
			} else {
				final Map<String, Object> schemaMap = new LinkedHashMap<>();
				schemaMap.put("anyOf", schemas);
				contentMap.put("schema",schemaMap);
			}
		}
		if(encoding != null && !encoding.isEmpty()) {
			contentMap.put("encoding", encoding);
		}
		return contentMap;
	}

}
