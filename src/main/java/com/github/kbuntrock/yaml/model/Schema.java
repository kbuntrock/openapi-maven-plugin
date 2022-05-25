package com.github.kbuntrock.yaml.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.utils.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schema {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> required;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Property> properties;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("enum")
    private List<String> enumValues;

    public Schema() {
    }

    public Schema(DataObject dataObject) {
        type = dataObject.getOpenApiType().getValue();

        // LinkedHashMap to keep the order of the class
        properties = new LinkedHashMap<>();

        List<Field> fields = ReflexionUtils.getAllNonStaticFields(new ArrayList<>(), dataObject.getJavaClass());
        if (!fields.isEmpty() && !dataObject.getJavaClass().isEnum()) {

            for (Field field : fields) {
                Property property = new Property();
                property.setName(field.getName());
                OpenApiDataType openApiDataType = OpenApiDataType.fromJavaClass(field.getType());
                if (field.getType().isAssignableFrom(Map.class)) {
                    property.setType(openApiDataType.getValue());
                    property.setAdditionalProperties(extractMapValueType(field));
                } else if (OpenApiDataType.OBJECT == openApiDataType) {
                    property.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + field.getType().getSimpleName());
                } else {
                    property.setType(openApiDataType.getValue());
                    OpenApiDataFormat format = openApiDataType.getFormat();
                    if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                        property.setFormat(format.getValue());
                    }
                    if (OpenApiDataType.ARRAY == openApiDataType) {
                        extractArrayType(field, property, dataObject);
                    }
                }

                extractConstraints(field, property);
                properties.put(property.getName(), property);

            }
        }

        List<String> enumItemValues = dataObject.getEnumItemValues();
        if (enumItemValues != null && !enumItemValues.isEmpty()) {
            enumValues = enumItemValues;
        }

        required = properties.values().stream()
                .filter(Property::isRequired).map(Property::getName).collect(Collectors.toList());
    }

    private Property extractMapValueType(Field field) {
        Property additionalProperty = new Property();
        DataObject dataObject = new DataObject(field.getType(), ((ParameterizedType) field.getGenericType()));
        if (dataObject.getMapValueType().isPureObject()) {
            additionalProperty.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getMapValueType().getJavaClass().getSimpleName());
        } else {
            additionalProperty.setType(dataObject.getMapValueType().getOpenApiType().getValue());
            OpenApiDataFormat format = dataObject.getMapValueType().getOpenApiType().getFormat();
            if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                additionalProperty.setFormat(format.getValue());
            }
        }
        return additionalProperty;
    }

    private void extractArrayType(Field field, Property property, DataObject source) {
        property.setUniqueItems(true);
        DataObject item;

        if (field.getType().isArray()) {
            item = new DataObject(field.getType(), null);
        } else {
            item = new DataObject(field.getType(), getContextualParameterizedType(field, source));
        }
        Map<String, String> items = new LinkedHashMap<>();
        if (item.getArrayItemDataObject().getJavaClass().isEnum() || item.getArrayItemDataObject().isPureObject()) {
            items.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, OpenApiConstants.OBJECT_REFERENCE_PREFIX + item.getArrayItemDataObject().getJavaClass().getSimpleName());
        } else {
            items.put(OpenApiConstants.TYPE, item.getArrayItemDataObject().getOpenApiType().getValue());
        }
        property.setItems(items);
    }

    /**
     * Get the parameterized Type, or the parameterized contextual one if the default is a generic.
     *
     * @param field  field in the source dataObject
     * @param source source dataObject
     * @return a ParameterizedType
     */
    private ParameterizedType getContextualParameterizedType(final Field field, final DataObject source) {
        if (source.isGenericallyTyped() && field.getGenericType() instanceof ParameterizedType) {
            // It is possible that we will not substitute anything. In that cas, the substitution parameterized type
            // will be equivalent to the source one.
            ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(((ParameterizedType) field.getGenericType()));
            for (int i = 0; i < substitution.getActualTypeArguments().length; i++) {
                if (source.getGenericNameToClassMap().containsKey(substitution.getActualTypeArguments()[i].getTypeName())) {
                    substitution.getActualTypeArguments()[i] =
                            source.getGenericNameToClassMap().get(substitution.getActualTypeArguments()[i].getTypeName());
                }
            }
            return substitution;
        }
        return null;
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
}
