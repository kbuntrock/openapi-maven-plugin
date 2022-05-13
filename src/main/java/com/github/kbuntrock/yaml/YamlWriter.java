package com.github.kbuntrock.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.kbuntrock.TagLibrary;
import com.github.kbuntrock.configuration.ApiConfiguration;
import com.github.kbuntrock.model.DataObject;
import com.github.kbuntrock.model.Endpoint;
import com.github.kbuntrock.model.ParameterObject;
import com.github.kbuntrock.model.Tag;
import com.github.kbuntrock.utils.*;
import com.github.kbuntrock.yaml.model.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class YamlWriter {

    private static final ObjectMapper om = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));

    private final Log logger = Logger.INSTANCE.getLogger();

    private final ClassLoader projectClassLoader;

    private final ApiConfiguration apiConfiguration;

    private final MavenProject mavenProject;

    public YamlWriter(final ClassLoader projectClassLoader, final MavenProject mavenProject, final ApiConfiguration apiConfiguration) {
        this.projectClassLoader = projectClassLoader;
        this.apiConfiguration = apiConfiguration;
        this.mavenProject = mavenProject;
    }

    public void write(File file, TagLibrary tagLibrary) throws IOException {
        Specification specification = new Specification();
        Info info = new Info();
        info.setTitle(mavenProject.getName());
        info.setVersion(mavenProject.getVersion());
        specification.setInfo(info);

        var server = new Server();
        server.setUrl("");
        specification.getServers().add(server);

        specification.setTags(tagLibrary.getTags().stream()
                .map(x -> new TagElement(x.computeConfiguredName(apiConfiguration))).collect(Collectors.toList()));

        specification.setPaths(createPaths(tagLibrary));

        specification.getComponents().put("schemas", createSchemas(tagLibrary));

        om.writeValue(file, specification);
    }

    private Map<String, Map<String, Operation>> createPaths(TagLibrary tagLibrary) {
        Map<String, Map<String, Operation>> paths = new LinkedHashMap<>();

        for (Tag tag : tagLibrary.getTags()) {

            // List of operations, which will be sorted before storing them by path. In order to keep a deterministic generation.
            List<Operation> operations = new ArrayList<>();

            for (Endpoint endpoint : tag.getEndpoints().stream().sorted(Comparator.comparing(Endpoint::getPath)).collect(Collectors.toList())) {
                paths.computeIfAbsent(endpoint.getPath(), k -> new LinkedHashMap<>());

                Operation operation = new Operation();
                operations.add(operation);
                operation.setName(endpoint.getType().name());
                operation.setPath(endpoint.getPath());
                operation.getTags().add(tag.computeConfiguredName(apiConfiguration));
                operation.setOperationId(endpoint.computeConfiguredName(apiConfiguration));

                // -------------------------
                // ----- PARAMETERS part----
                // -------------------------

                // All parameters which are not in the body
                for (ParameterObject parameter : endpoint.getParameters().stream()
                        .filter(x -> ParameterLocation.BODY != x.getLocation()).collect(Collectors.toList())) {
                    ParameterElement parameterElement = new ParameterElement();
                    parameterElement.setName(parameter.getName());
                    parameterElement.setIn(parameter.getLocation().toString().toLowerCase(Locale.ENGLISH));
                    parameterElement.setRequired(parameter.isRequired());
                    Property schema = new Property();
                    if (parameter.getJavaType().isEnum()) {
                        schema.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + parameter.getJavaType().getSimpleName());
                    } else {
                        schema.setType(parameter.getOpenApiType().getValue());
                        OpenApiDataFormat format = parameter.getOpenApiType().getFormat();
                        if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                            schema.setFormat(format.getValue());
                        }
                    }

                    // array in query or path parameters are not supported
                    if (OpenApiDataType.ARRAY == parameter.getOpenApiType()) {
                        logger.warn("Array types in path or query parameter are not allowed : "
                                + endpoint.getPath() + " - " + endpoint.getType());
                    }
                    parameterElement.setSchema(schema);
                    operation.getParameters().add(parameterElement);
                }

                // There can be only one body
                List<ParameterObject> bodies = endpoint.getParameters().stream().filter(x -> ParameterLocation.BODY == x.getLocation())
                        .collect(Collectors.toList());
                if (bodies.size() > 1) {
                    logger.warn("More than one body is not allowed : "
                            + endpoint.getPath() + " - " + endpoint.getType());
                }
                if (!bodies.isEmpty()) {
                    ParameterObject body = bodies.get(0);
                    RequestBody requestBody = new RequestBody();
                    operation.setRequestBody(requestBody);
                    Content requestBodyContent = Content.fromDataObject(body);
                    if (body.getFormat() != null) {
                        requestBody.getContent().put(body.getFormat(), requestBodyContent);
                    } else if (apiConfiguration.isDefaultProduceConsumeGuessing()) {
                        requestBody.getContent().put(ProduceConsumeUtils.getDefaultValue(body), requestBodyContent);
                    } else {
                        requestBody.getContent().put("*/*", requestBodyContent);
                    }

                }

                // -------------------------
                // ----- RESPONSE part----
                // -------------------------

                Response response = new Response();
                response.setCode(endpoint.getResponseCode());
                if (endpoint.getResponseObject() != null) {
                    Content responseContent = Content.fromDataObject(endpoint.getResponseObject());
                    if (endpoint.getResponseFormat() != null) {
                        response.getContent().put(endpoint.getResponseFormat(), responseContent);
                    } else if (apiConfiguration.isDefaultProduceConsumeGuessing()) {
                        response.getContent().put(ProduceConsumeUtils.getDefaultValue(endpoint.getResponseObject()), responseContent);
                    } else {
                        response.getContent().put("*/*", responseContent);
                    }

                }
                operation.getResponses().put(response.getCode(), response);

            }

            // We now order operations by types :
            operations = operations.stream().sorted(Comparator.comparing(Operation::getName)).collect(Collectors.toList());
            // And map them to their path
            for (Operation operation : operations) {
                paths.get(operation.getPath()).put(operation.getName().toLowerCase(), operation);
            }

        }
        return paths;
    }

    private Map<String, Schema> createSchemas(TagLibrary library) {
        List<DataObject> ordered = library.getSchemaObjects().stream()
                .sorted(Comparator.comparing(p -> p.getJavaType().getSimpleName())).collect(Collectors.toList());

        // LinkedHashMap to keep alphabetical order
        Map<String, Schema> schemas = new LinkedHashMap<>();
        for (DataObject dataObject : ordered) {
            Schema schema = new Schema();
            schemas.put(dataObject.getJavaType().getSimpleName(), schema);
            schema.setType(dataObject.getOpenApiType().getValue());

            // LinkedHashMap to keep the order of the class
            Map<String, Property> properties = new LinkedHashMap<>();
            schema.setProperties(properties);

            List<Field> fields = ReflexionUtils.getAllNonStaticFields(new ArrayList<>(), dataObject.getJavaType());
            if (!fields.isEmpty() && !dataObject.getJavaType().isEnum()) {

                for (Field field : fields) {
                    Property property = new Property();
                    property.setName(field.getName());
                    OpenApiDataType openApiDataType = OpenApiDataType.fromJavaType(field.getType());
                    if(field.getType().isAssignableFrom(Map.class)) {
                        property.setType(openApiDataType.getValue());
                        property.setAdditionalProperties(extractMapValueType(field));
                    } else if(OpenApiDataType.OBJECT == openApiDataType) {
                        property.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + field.getType().getSimpleName());
                    } else {
                        property.setType(openApiDataType.getValue());
                        OpenApiDataFormat format = openApiDataType.getFormat();
                        if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                            property.setFormat(format.getValue());
                        }
                        if (OpenApiDataType.ARRAY == openApiDataType) {
                            extractArrayType(field, property);
                        }
                    }

                    extractConstraints(field, property);
                    properties.put(property.getName(), property);

                }
            }

            List<String> enumItemValues = dataObject.getEnumItemValues();
            if (enumItemValues != null && !enumItemValues.isEmpty()) {
                schema.setEnumValues(enumItemValues);
            }

            schema.setRequired(schema.getProperties().values().stream()
                    .filter(Property::isRequired).map(Property::getName).collect(Collectors.toList()));
        }
        return schemas;
    }

    private Property extractMapValueType(Field field) {
        Property additionalProperty = new Property();
        DataObject dataObject = new DataObject();
        dataObject.setJavaType(field.getType(),  ((ParameterizedType) field.getGenericType()), projectClassLoader);
        if(dataObject.getMapValueType().isPureObject()) {
            additionalProperty.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + dataObject.getMapValueType().getJavaType().getSimpleName());
        } else {
            additionalProperty.setType(dataObject.getMapValueType().getOpenApiType().getValue());
            OpenApiDataFormat format = dataObject.getMapValueType().getOpenApiType().getFormat();
            if (OpenApiDataFormat.NONE != format && OpenApiDataFormat.UNKNOWN != format) {
                additionalProperty.setFormat(format.getValue());
            }
        }
        return additionalProperty;
    }

    private void extractArrayType(Field field, Property property) {
        property.setUniqueItems(true);
        DataObject item = new DataObject();
        if (field.getType().isArray()) {
            item.setJavaType(field.getType(), null, projectClassLoader);
        } else {
            item.setJavaType(field.getType(), ((ParameterizedType) field.getGenericType()), projectClassLoader);
        }
        Map<String, String> items = new LinkedHashMap<>();
        if (item.getArrayItemDataObject().getJavaType().isEnum() || item.getArrayItemDataObject().getOpenApiType() == OpenApiDataType.OBJECT) {
            items.put(OpenApiConstants.OBJECT_REFERENCE_DECLARATION, OpenApiConstants.OBJECT_REFERENCE_PREFIX + item.getArrayItemDataObject().getJavaType().getSimpleName());
        } else {
            items.put(OpenApiConstants.TYPE, item.getArrayItemDataObject().getOpenApiType().getValue());
        }
        property.setItems(items);
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
}

