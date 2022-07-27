package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kbuntrock.javadoc.ClassDocumentation;
import com.github.kbuntrock.javadoc.JavadocMap;
import com.github.kbuntrock.javadoc.JavadocWrapper;
import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.reflection.GenericArrayTypeImpl;
import com.github.kbuntrock.reflection.ParameterizedTypeImpl;
import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.utils.OpenApiConstants;
import com.github.kbuntrock.utils.OpenApiDataFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schema {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String description;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<String> required;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String type;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String format;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected Map<String, Property> properties;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("enum")
    protected List<String> enumValues;
    // Used in case of a Map object
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Schema additionalProperties;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(OpenApiConstants.OBJECT_REFERENCE_DECLARATION)
    protected String reference;
    // Used in case of an array object
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Schema items;

    /**
     * If true, we cannot reference the main object (we are using this object is the "schemas" section).
     */
    @JsonIgnore
    private boolean mainReference = false;


    public Schema() {
    }

    public Schema(DataObject dataObject) {
        this(dataObject, false);
    }

    public Schema(DataObject dataObject, boolean mainReference) {

        this.mainReference = mainReference;

        // Javadoc handling
        ClassDocumentation classDocumentation = null;
        if (JavadocMap.INSTANCE.isPresent()) {
            classDocumentation = JavadocMap.INSTANCE.getJavadocMap().get(dataObject.getJavaClass().getCanonicalName());
            if (classDocumentation != null) {
                classDocumentation.inheritanceEnhancement(dataObject.getJavaClass(), ClassDocumentation.EnhancementType.FIELDS);
            }
            if (classDocumentation != null && mainReference) {
                Optional<String> optionalDescription = classDocumentation.getDescription();
                if (optionalDescription.isPresent()) {
                    description = optionalDescription.get();
                }
            }
        }

        if (dataObject.isMap()) {
            type = dataObject.getOpenApiType().getValue();
            additionalProperties = new Schema(dataObject.getMapValueType());

        } else if (dataObject.isOpenApiArray()) {
            type = dataObject.getOpenApiType().getValue();
            items = new Schema(dataObject.getArrayItemDataObject());

        } else if (!mainReference && dataObject.isReferenceObject()) {
            reference = OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getJavaClass().getSimpleName();

        } else if ((mainReference && dataObject.isReferenceObject() || dataObject.isGenericallyTypedObject())) {

            type = dataObject.getOpenApiType().getValue();

            // LinkedHashMap to keep the order of the class
            properties = new LinkedHashMap<>();

            List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), dataObject.getJavaClass());
            if (!fields.isEmpty() && !dataObject.isEnum()) {

                for (Field field : fields) {

                    DataObject propertyObject = new DataObject(getContextualType(field, dataObject));
                    Property property = new Property(propertyObject, false, field.getName());
                    extractConstraints(field, property);
                    properties.put(property.getName(), property);

                    // Javadoc handling
                    if (classDocumentation != null) {
                        JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc().get(field.getName());
                        if (javadocWrapper != null) {
                            Optional<String> desc = javadocWrapper.getDescription();
                            property.setDescription(desc.get());
                        }
                    }

                }
            }

            List<String> enumItemValues = dataObject.getEnumItemValues();
            if (enumItemValues != null && !enumItemValues.isEmpty()) {
                enumValues = enumItemValues;
                if (classDocumentation != null) {
                    StringBuilder sb = new StringBuilder();
                    if (description != null) {
                        sb.append(description);
                        sb.append("\n");
                    } else {
                        sb.append(dataObject.getJavaClass().getSimpleName());
                        sb.append("\n");
                    }
                    for (String value : enumItemValues) {
                        JavadocWrapper javadocWrapper = classDocumentation.getFieldsJavadoc().get(value);
                        if (javadocWrapper != null) {
                            Optional<String> desc = javadocWrapper.getDescription();
                            if (desc.isPresent()) {
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


        } else {
            type = dataObject.getOpenApiType().getValue();
            OpenApiDataFormat openApiDataFormat = dataObject.getOpenApiType().getFormat();
            if (OpenApiDataFormat.NONE != openApiDataFormat && OpenApiDataFormat.UNKNOWN != openApiDataFormat) {
                this.format = openApiDataFormat.getValue();
            }
        }
    }

    /**
     * Get the type, or the parameterized contextual one if the default is a generic.
     *
     * @param field  field in the source dataObject
     * @param source source dataObject
     * @return a tyoe
     */
    private Type getContextualType(final Field field, final DataObject source) {

        if (source.isGenericallyTyped()) {
            // It is possible that we will not substitute anything. In that cas, the substitution parameterized type
            // will be equivalent to the source one.
            if (field.getGenericType() instanceof ParameterizedType) {

                ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(((ParameterizedType) field.getGenericType()));
                doContextualSubstitution(substitution, source);
                return substitution;

            } else if (field.getGenericType() instanceof GenericArrayType) {
                GenericArrayType genericArrayType = (GenericArrayType) field.getGenericType();
                if (genericArrayType.getGenericComponentType() instanceof ParameterizedType) {
                    ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(
                            (ParameterizedType) genericArrayType.getGenericComponentType());
                    doContextualSubstitution(substitution, source);
                    GenericArrayType substitionArrayType = new GenericArrayTypeImpl(substitution);
                    return substitionArrayType;
                } else if (genericArrayType.getGenericComponentType() instanceof TypeVariable<?>) {
                    TypeVariable<?> typeVariable = (TypeVariable<?>) genericArrayType.getGenericComponentType();
                    if (source.getGenericNameToTypeMap().containsKey(typeVariable.getName())) {
                        GenericArrayType substitionArrayType = new GenericArrayTypeImpl(source.getGenericNameToTypeMap().get(typeVariable.getName()));
                        return substitionArrayType;
                    }
                } else {
                    throw new RuntimeException("Type : " + ((GenericArrayType) field.getGenericType()).getGenericComponentType().getClass().toString()
                            + " not handled in generic array contextual substitution.");
                }

            }
        }
        return field.getGenericType();
    }

    private void doContextualSubstitution(ParameterizedTypeImpl substitution, DataObject source) {
        for (int i = 0; i < substitution.getActualTypeArguments().length; i++) {
            if (source.getGenericNameToTypeMap().containsKey(substitution.getActualTypeArguments()[i].getTypeName())) {
                substitution.getActualTypeArguments()[i] =
                        source.getGenericNameToTypeMap().get(substitution.getActualTypeArguments()[i].getTypeName());
            }
        }
    }

    private void extractConstraints(Field field, Property property) {
        Size size = field.getAnnotation(Size.class);
        if (size != null) {
            property.setMinLength(size.min());
            if (size.max() != Integer.MAX_VALUE) {
                property.setMaxLength(size.max());
            }
        }

        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null) {
            property.setRequired(true);
        }
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Schema getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Schema additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Schema getItems() {
        return items;
    }

    public void setItems(Schema items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
