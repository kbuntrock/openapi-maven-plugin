package io.github.kbuntrock.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.utils.*;
import io.github.kbuntrock.yaml.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class YamlWriter {

    private static String SERVERS_FIELD = "servers";
    private static String SECURITY_FIELD = "security";
    private static String EXTERNAL_DOC_FIELD = "externalDocs";

    private static final ObjectMapper om = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));

    private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

    private final Log logger = Logger.INSTANCE.getLogger();

    private final ApiConfiguration apiConfiguration;

    private final MavenProject mavenProject;

    private Optional<JsonNode> freefields = Optional.empty();

    public YamlWriter(final MavenProject mavenProject, final ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
        this.mavenProject = mavenProject;
    }

    private void parseFreeFields() {
        if (!StringUtils.isEmpty(apiConfiguration.getFreeFields())) {
            try {
                freefields = Optional.ofNullable(jsonObjectMapper.readTree(apiConfiguration.getFreeFields()));
            } catch (JsonProcessingException e) {
                throw new MojoRuntimeException("Free fields json configuration cannot be read", e);
            }
        }
    }

    private void populateSpecificationFreeFields(final Specification specification, final Optional<JsonNode> freefields) {

        if (freefields.isPresent() && freefields.get().get("servers") != null) {
            specification.setServers(freefields.get().get("servers"));
        } else {
            Server server = new Server();
            server.setUrl("");
            specification.setServers(Collections.singletonList(server));
        }

        if (freefields.isPresent()) {
            if (freefields.get().get("security") != null) {
                specification.setSecurity(freefields.get().get("security"));
            }
            if (freefields.get().get("externalDocs") != null) {
                specification.setExternalDocs(freefields.get().get("externalDocs"));
            }
        }
    }

    public void write(File file, TagLibrary tagLibrary) throws IOException {

        parseFreeFields();

        Specification specification = new Specification();
        Info info = new Info(mavenProject.getName(), mavenProject.getVersion(), freefields);
        specification.setInfo(info);

        populateSpecificationFreeFields(specification, freefields);

        specification.setTags(tagLibrary.getTags().stream()
                .map(x -> {
                    if (JavadocMap.INSTANCE.isPresent()) {
                        ClassDocumentation classDocumentation = JavadocMap.INSTANCE.getJavadocMap().get(x.getClazz().getCanonicalName());
                        if (classDocumentation != null) {
                            classDocumentation.inheritanceEnhancement(x.getClazz(), ClassDocumentation.EnhancementType.METHODS);
                            Optional<String> description = classDocumentation.getDescription();
                            if (description.isPresent()) {
                                return new TagElement(x.computeConfiguredName(apiConfiguration), description.get());
                            }

                        }
                    }

                    return new TagElement(x.computeConfiguredName(apiConfiguration), null);
                }).collect(Collectors.toList()));

        specification.setPaths(createPaths(tagLibrary));

        Map<String, Schema> schemaSection = createSchemaSection(tagLibrary);
        if (!schemaSection.isEmpty()) {
            specification.getComponents().put("schemas", schemaSection);
        }

        if (freefields.isPresent() && freefields.get().get("components") != null &&
                freefields.get().get("components").get("securitySchemes") != null) {
            specification.getComponents().put("securitySchemes", freefields.get().get("components").get("securitySchemes"));
        }

        om.writeValue(file, specification);
    }

    private Map<String, Map<String, Operation>> createPaths(TagLibrary tagLibrary) {
        Map<String, Map<String, Operation>> paths = new LinkedHashMap<>();

        Set<String> operationIds = new HashSet<>();

        for (Tag tag : tagLibrary.getTags()) {

            ClassDocumentation classDocumentation = JavadocMap.INSTANCE.isPresent() ?
                    JavadocMap.INSTANCE.getJavadocMap().get(tag.getClazz().getCanonicalName()) : null;
            // There is no need to try to enhance with the abstract or interfaces classes the documentation here.
            // It has already been made when we were writing the tags

            // List of operations, which will be sorted before storing them by path. In order to keep a deterministic generation.
            List<Operation> operations = new ArrayList<>();

            for (Endpoint endpoint : tag.getEndpoints().stream().sorted(Comparator.comparing(Endpoint::getPath)).collect(Collectors.toList())) {
                paths.computeIfAbsent(endpoint.getPath(), k -> new LinkedHashMap<>());

                Operation operation = new Operation();
                operations.add(operation);
                operation.setName(endpoint.getType().name());
                operation.setPath(endpoint.getPath());
                String computedTagName = tag.computeConfiguredName(apiConfiguration);
                operation.getTags().add(computedTagName);
                operation.setOperationId(apiConfiguration.getOperationIdHelper().toOperationId(tag.getName(), computedTagName, endpoint.getName()));
                if (apiConfiguration.isLoopbackOperationName()) {
                    operation.setLoopbackOperationName(endpoint.getName());
                }
                operation.setDeprecated(endpoint.isDeprecated());

                // Javadoc to description
                JavadocWrapper methodJavadoc = null;
                if (classDocumentation != null) {
                    methodJavadoc = classDocumentation.getMethodsJavadoc().get(endpoint.getIdentifier());
                    if (methodJavadoc != null) {
                        methodJavadoc.sortTags();
                        operation.setDescription(methodJavadoc.getJavadoc().getDescription().toText());
                    }
                }

                // Warning on paths
                if (!operation.getPath().startsWith("/")) {
                    Logger.INSTANCE.getLogger().warn("Operation " + operation.getOperationId()
                            + " path should start with a \"/\" (" + operation.getPath() + ")");
                }
                // Warning on operation Ids
                if (!operationIds.add(operation.getOperationId())) {
                    Logger.INSTANCE.getLogger().warn("Operation id \"" +
                            operation.getOperationId() + "\" (" + tag.getName() + ") should be unique");
                }

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
                    if (parameter.getJavaClass().isEnum()) {
                        schema.setReference(OpenApiConstants.OBJECT_REFERENCE_PREFIX + parameter.getJavaClass().getSimpleName());
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

                    // Javadoc handling
                    if (methodJavadoc != null) {
                        Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(parameterElement.getName());
                        if (parameterDoc.isPresent()) {
                            String description = parameterDoc.get().getContent().toText();
                            if (!description.isEmpty()) {
                                parameterElement.setDescription(parameterDoc.get().getContent().toText());
                            }
                        }
                    }

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

                    // Javadoc handling
                    if (methodJavadoc != null) {
                        Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(body.getName());
                        if (parameterDoc.isPresent()) {
                            String description = parameterDoc.get().getContent().toText();
                            if (!description.isEmpty()) {
                                requestBody.setDescription(parameterDoc.get().getContent().toText());
                            }
                        }
                    }

                }

                // -------------------------
                // ----- RESPONSE part----
                // -------------------------

                Response response = new Response();
                response.setCode(endpoint.getResponseCode(), apiConfiguration.getDefaultSuccessfulOperationDescription());
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

                // Javadoc handling
                if (methodJavadoc != null) {
                    Optional<JavadocBlockTag> returnDoc = methodJavadoc.getReturnBlockTag();
                    if (returnDoc.isPresent()) {
                        String description = returnDoc.get().getContent().toText();
                        if (!description.isEmpty()) {
                            response.setDescription(returnDoc.get().getContent().toText());
                        }
                    }
                }

                operation.getResponses().put(response.getCode(), response);

            }

            // We now order operations by types :
            operations = operations.stream().sorted(Comparator.comparing(Operation::getName)).collect(Collectors.toList());
            // And map them to their path
            for (Operation operation : operations) {
                Operation previousOperation = paths.get(operation.getPath()).put(operation.getName().toLowerCase(), operation);
                if (previousOperation != null) {
                    throw new MojoRuntimeException("More than one operation mapped on " + operation.getName() + " : " + operation.getPath() + " in tag " + tag.getName());
                }
            }

        }
        return paths;
    }

    private Map<String, Schema> createSchemaSection(TagLibrary library) {
        List<DataObject> ordered = library.getSchemaObjects().stream()
                .sorted(Comparator.comparing(p -> p.getJavaClass().getSimpleName())).collect(Collectors.toList());

        // LinkedHashMap to keep alphabetical order
        Map<String, Schema> schemas = new LinkedHashMap<>();
        for (DataObject dataObject : ordered) {
            Schema schema = new Schema(dataObject, true);
            schemas.put(dataObject.getJavaClass().getSimpleName(), schema);
        }
        return schemas;
    }
}

