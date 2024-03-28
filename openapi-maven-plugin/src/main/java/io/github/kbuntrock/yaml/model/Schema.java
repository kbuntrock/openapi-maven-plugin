package io.github.kbuntrock.yaml.model;

import static io.github.kbuntrock.TagLibrary.METHOD_GET_PREFIX;
import static io.github.kbuntrock.TagLibrary.METHOD_GET_PREFIX_SIZE;
import static io.github.kbuntrock.TagLibrary.METHOD_IS_PREFIX;
import static io.github.kbuntrock.TagLibrary.METHOD_IS_PREFIX_SIZE;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.JavaClassAnalyser;
import io.github.kbuntrock.TagLibraryHolder;
import io.github.kbuntrock.configuration.NullableConfigurationHolder;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.ClassDocumentation.EnhancementType;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiConstants;
import io.github.kbuntrock.utils.OpenApiResolvedType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.utils.UnwrappingType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schema {

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


	public Schema() {
	}

	public Schema(final DataObject dataObject, final Set<String> exploredSignatures) {
		this(dataObject, false, exploredSignatures, null, null);
	}

	public Schema(final DataObject wrappedDataObject, final boolean mainReference, final Set<String> exploredSignatures,
		final DataObject parentDataObject,
		final String parentFieldName) {

		final DataObject dataObject = OpenApiTypeResolver.INSTANCE.unwrapDataObject(wrappedDataObject, UnwrappingType.SCHEMA);

		this.mainReference = mainReference;

		// Javadoc handling
		ClassDocumentation classDocumentation = null;
		if(JavadocMap.INSTANCE.isPresent()) {
			classDocumentation = JavadocMap.INSTANCE.getJavadocMap().get(dataObject.getJavaClass().getCanonicalName());
			if(classDocumentation != null) {
				classDocumentation.inheritanceEnhancement(dataObject.getJavaClass(), EnhancementType.BOTH);
			}
			if(classDocumentation != null && mainReference) {
				final Optional<String> optionalDescription = classDocumentation.getDescription();
				if(optionalDescription.isPresent()) {
					description = optionalDescription.get();
				}
			}
		}

		if(dataObject.isMap()) {
			type = dataObject.getOpenApiResolvedType();
			additionalProperties = new Schema(dataObject.getMapValueType(), false, exploredSignatures, parentDataObject, parentFieldName);

		} else if(dataObject.isOpenApiArray()) {
			type = dataObject.getOpenApiResolvedType();
			items = new Schema(dataObject.getArrayItemDataObject(), false, exploredSignatures, parentDataObject, parentFieldName);
			uniqueItems = dataObject.getUniqueItems();

		} else if(!mainReference && dataObject.isReferenceObject()) {
			final DataObject referenceDataObject = TagLibraryHolder.INSTANCE.getTagLibrary().getClassToSchemaObject()
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
				final String objectSignature =
					parentDataObject.getJavaClass().getSimpleName() + "_" + parentFieldName + "_" + dataObject.getSignature();
				if(!exploredSignatures.add(objectSignature)) {
					// The fieldname + signature has already be seen. We are in a recursive loop
					// We will have to write this field in the schema section.
					referenceSignature = parentDataObject.getJavaClass().getSimpleName() + "_" + dataObject.getSchemaRecursiveSuffix();
					AdditionnalSchemaLibrary.addDataObject(referenceSignature, dataObject);
					forcedReference = true;
				}
			}

			if(!forcedReference) {
				type = dataObject.getOpenApiResolvedType();

				if(!type.isCompleteNode()) {

					// LinkedHashMap to keep the order of the class
					properties = new LinkedHashMap<>();

					final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), dataObject.getJavaClass());
					if(!fields.isEmpty() && !dataObject.isEnum()) {

						for(final Field field : fields) {
							if(field.isAnnotationPresent(JsonIgnore.class)) {
								// Field is tagged ignore. No need to document it.
								continue;
							}
							// Jackson @JsonProperty annotation handling
							String propertyFieldName = field.getName();
							final JsonProperty jsonPropertyField = field.getAnnotation(JsonProperty.class);
							if(jsonPropertyField != null && !jsonPropertyField.value().isEmpty()) {
								propertyFieldName = jsonPropertyField.value();
							}

							final DataObject wrappedPropertyObject = new DataObject(dataObject.getContextualType(field.getGenericType()));
							final DataObject propertyObject = OpenApiTypeResolver.INSTANCE.unwrapDataObject(wrappedPropertyObject,
								UnwrappingType.SCHEMA);
							final Property property = new Property(propertyObject, false, propertyFieldName, exploredSignatures,
								dataObject);
							extractConstraints(field, property);
							properties.put(property.getName(), property);

							// Javadoc handling
							if(classDocumentation != null) {
								final JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc().get(field.getName());
								if(javadocWrapper != null) {
									final Optional<String> desc = javadocWrapper.getDescription();
									property.setDescription(desc.get());
								}
							}
						}
					}
					if(dataObject.getJavaClass().isInterface()) {
						final List<Method> methods = Arrays.stream(dataObject.getJavaClass().getMethods()).collect(Collectors.toList());
						methods.sort(Comparator.comparing(a -> a.getName()));
						for(final Method method : methods) {
							final boolean methodStartWithGet =
								method.getName().startsWith(METHOD_GET_PREFIX) && method.getName().length() != METHOD_GET_PREFIX_SIZE;
							if(method.getParameters().length == 0 && method.getGenericReturnType() != null
								&& (methodStartWithGet || (method.getName().startsWith(METHOD_IS_PREFIX)
								&& method.getName().length() != METHOD_IS_PREFIX_SIZE))) {

								String name;
								if(methodStartWithGet) {
									name = method.getName().replaceFirst("get", "");
								} else {
									name = method.getName().replaceFirst("is", "");
								}
								Logger.INSTANCE.getLogger()
									.debug(dataObject.getJavaClass().getSimpleName() + " method name : " + method.getName() + " - " + name);
								name = name.substring(0, 1).toLowerCase() + name.substring(1);

								final DataObject propertyObject = new DataObject(
									dataObject.getContextualType(method.getGenericReturnType()));
								final Property property = new Property(propertyObject, false, name, exploredSignatures, dataObject);
								properties.put(property.getName(), property);

								// Javadoc handling
								if(classDocumentation != null) {
									final JavadocWrapper javadocWrapper = classDocumentation.getMethodsJavadoc()
										.get(JavaClassAnalyser.createIdentifier(method));
									if(javadocWrapper != null) {
										final Optional<String> desc = javadocWrapper.getDescription();
										property.setDescription(desc.get());
									}
								}
							}
						}

					}

					final List<String> enumItemValues = dataObject.getEnumItemValues();
					if(enumItemValues != null && !enumItemValues.isEmpty()) {
						enumValues = enumItemValues;
						enumNames = dataObject.getEnumItemNames();
						if(classDocumentation != null) {
							final StringBuilder sb = new StringBuilder();
							if(description != null) {
								sb.append(description);
								sb.append("\n");
							} else {
								sb.append(dataObject.getJavaClass().getSimpleName());
								sb.append("\n");
							}
							for(int i = 0; i < enumItemValues.size(); i++) {
								final String value = enumNames == null ? enumItemValues.get(i) : enumNames.get(i);
								final JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc().get(value);
								if(javadocWrapper != null) {
									final Optional<String> desc = javadocWrapper.getDescription();
									if(desc.isPresent()) {
										sb.append("  * ");
										sb.append("`");
										sb.append(value);
										sb.append("` - ");
										sb.append(desc.get());
										sb.append("\n");
									}
								}
							}
							description = sb.toString();
						}
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

	private void extractConstraints(final Field field, final Property property) {
		final Size size = field.getAnnotation(Size.class);
		if(size != null) {
			property.setMinLength(size.min());
			if(size.max() != Integer.MAX_VALUE) {
				property.setMaxLength(size.max());
			}
		}

		if(NullableConfigurationHolder.hasNonNullAnnotation(field)) {
			property.setRequired(true);
		} else if(NullableConfigurationHolder.hasNullableAnnotation(field)) {
			property.setRequired(false);
		} else {
			property.setRequired(NullableConfigurationHolder.isDefaultNonNullableFields());
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
		if(enumNames != null && !enumNames.isEmpty()) {
			map.put("x-enumNames", enumNames);
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
