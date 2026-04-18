package io.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.BeanDefinitionUtils;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.ClassDocumentation.EnhancementType;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Flow;
import io.github.kbuntrock.reflection.BeanDefinition;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiConstants;
import io.github.kbuntrock.utils.OpenApiResolvedType;
import io.github.kbuntrock.utils.UnwrappingType;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schema {

	@JsonIgnore
	protected String summary;
	@JsonIgnore
	protected String description;
	@JsonIgnore
	protected List<String> required;
	// The type is unwrapped
	@JsonIgnore
	protected OpenApiResolvedType type;
	@JsonIgnore
	protected Map<String, Property> properties;
	@JsonIgnore
	protected List<String> enumValues;
	@JsonIgnore
	protected List<String> enumNames;
	@JsonIgnore
	protected List<String> enumDescriptions;
	// Used in case of a Map object
	@JsonIgnore
	protected Schema additionalProperties;
	@JsonIgnore
	protected String reference;
	// Used in case of an array object
	@JsonIgnore
	protected Schema items;
	// Used in case of an array object
	@JsonIgnore
	private Boolean uniqueItems;

	/**
	 * If true, we cannot reference the main object (we are using this object is the "schemas" section).
	 */
	@JsonIgnore
	private boolean mainReference = false;
	@JsonIgnore
	private DataObject parentDataObject;
	@JsonIgnore
	private String parentFieldName;

	/**
	 * The json/yaml representation depends from the configuration.
	 */
	@JsonIgnore
	protected ApiConfiguration apiConfiguration;

	@JsonIgnore
	protected ApiContext context;

	public Schema(final ApiContext context, final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
		this.context = context;
	}

	public Schema(final DataObject dataObject, final Set<String> exploredSignatures,
		final TagLibrary tagLibrary) {
		this(dataObject, false, exploredSignatures, null, null, tagLibrary);
	}

	public Schema(final DataObject wrappedDataObject,
		final boolean mainReference,
		final Set<String> exploredSignatures,
		final DataObject parentDataObject,
		final String parentFieldName,
		final TagLibrary tagLibrary) {
		this(wrappedDataObject, mainReference, exploredSignatures, parentDataObject, parentFieldName, tagLibrary, false);
	}

	/**
	 *
	 * @param wrappedDataObject
	 * @param mainReference
	 *            true if we are writing the components/schemas section
	 * @param exploredSignatures
	 * @param parentDataObject
	 * @param parentFieldName
	 */
	public Schema(final DataObject wrappedDataObject,
		final boolean mainReference,
		final Set<String> exploredSignatures,
		final DataObject parentDataObject,
		final String parentFieldName,
		final TagLibrary tagLibrary,
		final boolean forNonGenericSchemaSection) {

		final DataObject dataObject = tagLibrary.getOpenApiTypeResolver().unwrapDataObject(wrappedDataObject,
			UnwrappingType.SCHEMA);

		this.apiConfiguration = tagLibrary.getApiConfiguration();
		this.context = tagLibrary.getContext();

		this.mainReference = mainReference;

		// Javadoc handling
		ClassDocumentation classDocumentation = null;
		if(tagLibrary.hasJavadocMap()) {
			classDocumentation = tagLibrary.getJavadocMap().get(dataObject.getJavaClass().getCanonicalName());
			if(classDocumentation != null) {
				classDocumentation.inheritanceEnhancement(dataObject.getJavaClass(), EnhancementType.BOTH,
					tagLibrary.getJavadocMap());
			}
			if(classDocumentation != null && mainReference) {
				final Optional<String> optionalDescription = classDocumentation.getDescription();
				if(optionalDescription.isPresent()) {
					description = optionalDescription.get();
				}
			}
		}
		// Swagger annotation on the class
		MergedAnnotations mergedClassAnnotations = context.getMergeAnnotationsHelper().from(dataObject.getJavaClass());
		final MergedAnnotation classSchemaAnnotation = mergedClassAnnotations.get("io.swagger.v3.oas.annotations.media.Schema");
		if(classSchemaAnnotation.isPresent()) {
			String swaggerDescription = classSchemaAnnotation.getString("description");
			if(!StringUtils.isEmpty(swaggerDescription)) {
				description = swaggerDescription;
			}
		}

		if(dataObject.isMap()) {
			type = dataObject.getOpenApiResolvedType();
			additionalProperties = new Schema(dataObject.getMapValueType(), false, exploredSignatures, parentDataObject,
				parentFieldName, tagLibrary);
		} else if(dataObject.isOpenApiArray()) {
			type = dataObject.getOpenApiResolvedType();
			items = new Schema(dataObject.getArrayItemDataObject(), false, exploredSignatures, parentDataObject, parentFieldName,
				tagLibrary);
			uniqueItems = dataObject.getUniqueItems();

		} else if(!mainReference && dataObject.isReferenceObject()) {
			final DataObject referenceDataObject = tagLibrary.getClassToSchemaObject()
				.get(dataObject.getJavaClass());
			if(referenceDataObject == null) {
				// Investigation on a rare bug where the reference is not found.
				throw new RuntimeException(
					"Writing schema but could not find a reference for class " + dataObject.getJavaClass().getSimpleName());
			}
			if(dataObject.getOpenApiResolvedType().isCompleteNode()) {
				reference = OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getOpenApiResolvedType().getModelName();
			} else {
				reference = OpenApiConstants.OBJECT_REFERENCE_PREFIX + referenceDataObject.getSchemaReferenceName();
			}

		} else if((mainReference && dataObject.isReferenceObject() || dataObject.isGenericallyTypedObject())) {

			boolean forcedReference = false;
			String referenceSignature = null;
			if(parentDataObject != null && parentFieldName != null) {
				final String objectSignature = parentDataObject.getJavaClass().getSimpleName() + "_" + parentFieldName + "_"
					+ dataObject.getSignature();
				if(!exploredSignatures.add(objectSignature)) {
					// The field name + signature has already be seen. We are in a recursive loop
					// We will have to write this field in the schema section.
					referenceSignature = parentDataObject.getJavaClass().getSimpleName() + "_"
						+ dataObject.getSchemaRecursiveSuffix();
					context.getAdditionnalSchemaLibrary().addDataObject(referenceSignature, dataObject);
					forcedReference = true;
				}
			}

			if(!forcedReference) {
				type = dataObject.getOpenApiResolvedType();

				if(!type.isCompleteNode()) {

					// LinkedHashMap to keep the order of the class
					properties = new LinkedHashMap<>();

					if(dataObject.isEnum()) {
						createEnumSchemaObject(dataObject, classDocumentation);
					} else {
						createRegularSchemaObject(exploredSignatures, tagLibrary, dataObject, classDocumentation,
							forNonGenericSchemaSection);
					}
					required = properties.values().stream()
						.filter(Property::isRequired).map(Property::getName).collect(Collectors.toList());
				}
			} else {
				// We are in a recursive loop case. We write the object as reference and we will have to add it to the schema section
				reference = OpenApiConstants.OBJECT_REFERENCE_PREFIX + referenceSignature;
			}

		} else {
			type = dataObject.getOpenApiResolvedType();
		}
	}

	private void createEnumSchemaObject(DataObject dataObject, ClassDocumentation classDocumentation) {
		final List<String> enumItemValues = dataObject.getEnumItemValues();
		if(enumItemValues != null && !enumItemValues.isEmpty()) {
			enumValues = enumItemValues;
			enumNames = dataObject.getEnumItemNames();
			if(classDocumentation != null) {
				enumDescriptions = new ArrayList<>();

				final StringBuilder sb = new StringBuilder();
				if(description != null) {
					sb.append(description);
					sb.append("\n");
				}
				for(int i = 0; i < enumItemValues.size(); i++) {
					String descriptionValue = "";
					final String value = enumNames == null ? enumItemValues.get(i) : enumNames.get(i);
					final JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc().get(value);
					if(javadocWrapper != null) {
						final Optional<String> desc = javadocWrapper.getDescription();
						if(desc.isPresent()) {
							descriptionValue = desc.get();

							if(apiConfiguration.getEnumListDescriptionEnabled()) {
								sb.append("  * ");
								sb.append("`");
								sb.append(value);
								sb.append("` - ");
								sb.append(desc.get());
								sb.append("\n");
							}
						}
					}
					enumDescriptions.add(descriptionValue);
				}
				description = sb.toString();
			}
		}
	}

	private void createRegularSchemaObject(Set<String> exploredSignatures, TagLibrary tagLibrary, DataObject dataObject,
		ClassDocumentation classDocumentation, boolean forNonGenericSchemaSection) {

		if(forNonGenericSchemaSection) {
			List<ChildObject> childProperties = dataObject.getChildObjects();
			for(ChildObject child : childProperties) {
				if(child.getBeanDefinition().compatibleWithFlow(dataObject.getFlow())) {
					final DataObject propertyObject = tagLibrary.getOpenApiTypeResolver().unwrapDataObject(
						child.getDataObject(),
						UnwrappingType.SCHEMA);
					final Property property = new Property(propertyObject, false, child.getName(), exploredSignatures,
						dataObject, tagLibrary);
					extractConstraints(dataObject, child.getBeanDefinition(), property);
					properties.put(property.getName(), property);
					// Javadoc and swagger annotations handling
					setPropertyDescription(child.getBeanDefinition(), classDocumentation, property);
				}
			}
		} else {
			List<ChildObject> childProperties = BeanDefinitionUtils.getPropertyObjectsToDocument(dataObject, context);
			for(ChildObject child : childProperties) {
				final DataObject propertyObject = tagLibrary.getOpenApiTypeResolver().unwrapDataObject(
					child.getDataObject(),
					UnwrappingType.SCHEMA);
				final Property property = new Property(propertyObject, false, child.getName(), exploredSignatures,
					dataObject, tagLibrary);
				extractConstraints(dataObject, child.getBeanDefinition(), property);
				properties.put(property.getName(), property);
				// Javadoc and swagger annotations handling
				setPropertyDescription(child.getBeanDefinition(), classDocumentation, property);
			}
		}

	}

	private void setPropertyDescription(BeanDefinition propertyDefinition, ClassDocumentation classDocumentation,
		Property property) {
		// Javadoc handling
		if(classDocumentation != null) {
			// Fields
			if(propertyDefinition.hasField()) {
				final JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc()
					.get(propertyDefinition.getField().getName());
				setPropertyDescriptionFromJavadocIfEmpty(javadocWrapper, property);
			}
			// Methods
			if(propertyDefinition.hasGetter()) {
				final JavadocWrapper javadocWrapper = classDocumentation.getMethodsJavadocByIdentifier()
					.get(JavaClassAnalyser.createMethodIdentifier(propertyDefinition.getGetter().getAnnotated()));
				setPropertyDescriptionFromJavadocIfEmpty(javadocWrapper, property);
			}
			if(propertyDefinition.hasSetter()) {
				final JavadocWrapper javadocWrapper = classDocumentation.getMethodsJavadocByIdentifier()
					.get(JavaClassAnalyser.createMethodIdentifier(propertyDefinition.getSetter().getAnnotated()));
				setPropertyDescriptionFromJavadocIfEmpty(javadocWrapper, property);
			}
			// Constructor
			if(propertyDefinition.hasConstructorParameter()) {
				// The javadoc parsing store the documentation in "fields".
				final JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc()
					.get(propertyDefinition.getInternalName());
				setPropertyDescriptionFromJavadocIfEmpty(javadocWrapper, property);
			}
		}

		// Swagger handling
		// Fields
		if(propertyDefinition.hasField()) {
			MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper()
				.from(propertyDefinition.getField().getAnnotated());
			setPropertyDescriptionFromSwaggerAnnotation(mergedAnnotations, property);
		}
		// Methods
		if(propertyDefinition.hasGetter()) {
			MergedAnnotations mergedAnnotations = context.getMergeAnnotationsHelper()
				.from(propertyDefinition.getGetter().getAnnotated());
			setPropertyDescriptionFromSwaggerAnnotation(mergedAnnotations, property);
		}
	}

	private static void setPropertyDescriptionFromSwaggerAnnotation(MergedAnnotations mergedAnnotations, Property property) {
		final MergedAnnotation schemaAnnotation = mergedAnnotations
			.get("io.swagger.v3.oas.annotations.media.Schema");
		if(schemaAnnotation.isPresent()) {
			String swaggerDescription = schemaAnnotation.getString("description");
			if(!StringUtils.isEmpty(swaggerDescription)) {
				property.setDescription(swaggerDescription);
			}
			String swaggerExample = schemaAnnotation.getString("example");
			if(!StringUtils.isEmpty(swaggerExample)) {
				property.setExample(swaggerExample);
			}
		}
	}

	private static void setPropertyDescriptionFromJavadocIfEmpty(JavadocWrapper javadocWrapper, Property property) {
		if(javadocWrapper != null) {
			if(property.getDescription() == null) {
				final Optional<String> desc = javadocWrapper.getDescription();
				property.setDescription(desc.orElse(null));
			}

			if(property.getSummary() == null) {
				final Optional<String> summary = javadocWrapper.getSummary();
				property.setSummary(summary.orElse(null));
			}
		}
	}

	private <T extends Annotation> T findAnnotationByClass(List<Annotation> annotations, Class<T> annotationType) {
		return (T) annotations.stream().filter(a -> a.annotationType().equals(annotationType)).findFirst().orElse(null);
	}

	private void extractConstraints(DataObject dataObject, final BeanDefinition beanDefinition, final Property property) {
		List<Annotation> annotations = new ArrayList<>();
		if(beanDefinition.hasField()) {
			annotations.addAll(Arrays.asList(beanDefinition.getField().getAnnotated().getAnnotations()));
		}
		if(beanDefinition.hasGetter()) {
			annotations.addAll(Arrays.asList(beanDefinition.getGetter().getAnnotated().getAnnotations()));
		}
		if(beanDefinition.hasSetter()) {
			annotations.addAll(Arrays.asList(beanDefinition.getSetter().getAnnotated().getAnnotations()));
		}

		final Size size = findAnnotationByClass(annotations, Size.class);
		if(size != null) {
			property.setMinLength(size.min());
			if(size.max() != Integer.MAX_VALUE) {
				property.setMaxLength(size.max());
			}
		}

		if(context.getNullableConfiguration().hasNonNullAnnotation(annotations)) {
			property.setRequired(true);
		} else if(context.getNullableConfiguration().hasNullableAnnotation(annotations)) {
			property.setRequired(false);
		} else {
			property.setRequired(context.getNullableConfiguration().isDefaultNonNullableFields());
		}

		if(Flow.INPUT_OUTPUT == dataObject.getFlow()) {
			if(Flow.OUTPUT == beanDefinition.getFlow()) {
				property.setReadWriteRule(ReadWriteRule.READ_ONLY);
			} else if(Flow.INPUT == beanDefinition.getFlow()) {
				property.setReadWriteRule(ReadWriteRule.WRITE_ONLY);
			}
		}
	}

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(final List<String> required) {
		this.required = required;
	}

	public OpenApiResolvedType getType() {
		return type;
	}

	public void setType(final OpenApiResolvedType type) {
		this.type = type;
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(final Map<String, Property> properties) {
		this.properties = properties;
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(final List<String> enumValues) {
		this.enumValues = enumValues;
	}

	public Schema getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(final Schema additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public Schema getItems() {
		return items;
	}

	public void setItems(final Schema items) {
		this.items = items;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	public void setUniqueItems(final Boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}

	/**
	 * It is impossible to mix @JsonAnyGetter + regular fields with a defined order.
	 * Therefore, the json object is manually crafted.
	 *
	 * @return
	 */
	@JsonAnyGetter
	public Map<String, Object> getJsonObject() {

		final Map<String, Object> map = new LinkedHashMap<>();

		// In OpenAPI 3.0, a $ref must not have sibling properties. When a reference
		// coexists with a description (or other fields), wrap the $ref inside an allOf
		// so that the description stays at the current level while the reference is
		// isolated inside the allOf entry.
		if(StringUtils.isNotBlank(reference) && description != null) {
			map.put("description", description);
			final Map<String, Object> refMap = new LinkedHashMap<>();
			refMap.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, reference);
			map.put("allOf", Collections.singletonList(refMap));
			return map;
		}

		// Elsewhere, resolved type only describe vaguely the type (object or array), and we write all the infos
		if(description != null) {
			map.put("description", description);
		}
		if(required != null && !required.isEmpty()) {
			map.put("required", required);
		}
		if(type != null) {
			for(final Entry<String, JsonNode> entry : type.getSchemaSection().entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		if(properties != null && !properties.isEmpty()) {
			map.put("properties", properties);
		}
		if(enumValues != null && !enumValues.isEmpty()) {
			map.put("enum", enumValues);
		}
		if(apiConfiguration.getEnumNameExtensionEnabled() && enumNames != null && !enumNames.isEmpty()) {
			map.put(apiConfiguration.getEnumNameExtensionValue(), enumNames);
		}
		if(apiConfiguration.getEnumDescriptionExtensionEnabled() && enumDescriptions != null && !enumDescriptions.isEmpty()) {
			map.put(apiConfiguration.getEnumDescriptionExtensionValue(), enumDescriptions);
		}
		if(additionalProperties != null) {
			map.put("additionalProperties", additionalProperties);
		}
		if(StringUtils.isNotBlank(reference)) {
			map.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, reference);
		}
		if(items != null) {
			map.put("items", items);
		}
		if(uniqueItems != null) {
			map.put("uniqueItems", uniqueItems);
		}
		return map;
	}

}
