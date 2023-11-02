package io.github.kbuntrock.yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.JsonConfigurationParserUtils;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiConstants;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.ParameterLocation;
import io.github.kbuntrock.utils.ProduceConsumeUtils;
import io.github.kbuntrock.yaml.model.Content;
import io.github.kbuntrock.yaml.model.Info;
import io.github.kbuntrock.yaml.model.Operation;
import io.github.kbuntrock.yaml.model.ParameterElement;
import io.github.kbuntrock.yaml.model.Property;
import io.github.kbuntrock.yaml.model.RequestBody;
import io.github.kbuntrock.yaml.model.Response;
import io.github.kbuntrock.yaml.model.Schema;
import io.github.kbuntrock.yaml.model.Server;
import io.github.kbuntrock.yaml.model.Specification;
import io.github.kbuntrock.yaml.model.TagElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class YamlWriter {

	private static final ObjectMapper om = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
		.enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));
	private static final String SERVERS_FIELD = "servers";
	private static final String SECURITY_FIELD = "security";
	private static final String EXTERNAL_DOC_FIELD = "externalDocs";
	private final Log logger = Logger.INSTANCE.getLogger();

	private final ApiConfiguration apiConfiguration;

	private final MavenProject mavenProject;

	private Optional<JsonNode> freefields = Optional.empty();
	private Map<String, JsonNode> defaultErrors;

	public YamlWriter(final MavenProject mavenProject, final ApiConfiguration apiConfiguration) {
		this.apiConfiguration = apiConfiguration;
		this.mavenProject = mavenProject;
	}

	private void populateSpecificationFreeFields(final Specification specification, final Optional<JsonNode> freefields) {

		if(freefields.isPresent() && freefields.get().get(SERVERS_FIELD) != null) {
			specification.setServers(freefields.get().get(SERVERS_FIELD));
		} else {
			final Server server = new Server();
			server.setUrl("");
			specification.setServers(Collections.singletonList(server));
		}

		if(freefields.isPresent()) {
			if(freefields.get().get(SECURITY_FIELD) != null) {
				specification.setSecurity(freefields.get().get(SECURITY_FIELD));
			}
			if(freefields.get().get(EXTERNAL_DOC_FIELD) != null) {
				specification.setExternalDocs(freefields.get().get(EXTERNAL_DOC_FIELD));
			}
		}
	}

	public void write(final File file, final TagLibrary tagLibrary) throws IOException {

		freefields = JsonConfigurationParserUtils.parse(mavenProject, apiConfiguration.getFreeFields());
		final Optional<JsonNode> defaultErrorsNode = JsonConfigurationParserUtils.parse(mavenProject, apiConfiguration.getDefaultErrors());
		if(defaultErrorsNode.isPresent()) {
			defaultErrors = new LinkedHashMap<>();
			final Iterator<Map.Entry<String, JsonNode>> iterator = defaultErrorsNode.get().fields();
			iterator.forEachRemaining(entry -> defaultErrors.put(entry.getKey(), entry.getValue()));
		}

		final Specification specification = new Specification();
		final Info info = new Info(mavenProject.getName(), mavenProject.getVersion(), freefields);
		specification.setInfo(info);

		populateSpecificationFreeFields(specification, freefields);

		specification.setTags(tagLibrary.getTags().stream()
			.map(x -> {
				if(JavadocMap.INSTANCE.isPresent()) {
					ClassDocumentation classDocumentation = JavadocMap.INSTANCE.getJavadocMap().get(x.getClazz().getCanonicalName());
					// Even if there is no declared class documentation, we may enhance it with javadoc on interface and/or abstract classes
					if(classDocumentation == null) {
						classDocumentation = new ClassDocumentation(x.getClazz().getCanonicalName(), x.getClazz().getSimpleName());
						JavadocMap.INSTANCE.getJavadocMap().put(x.getClazz().getCanonicalName(), classDocumentation);
					}
					logger.debug(
						"Class documentation found for tag " + x.getClazz().getSimpleName() + " ? " + (classDocumentation != null));

					classDocumentation.inheritanceEnhancement(x.getClazz(), ClassDocumentation.EnhancementType.METHODS);
					final Optional<String> description = classDocumentation.getDescription();
					if(description.isPresent()) {
						return new TagElement(x.computeConfiguredName(apiConfiguration), description.get());
					}
				}

				return new TagElement(x.computeConfiguredName(apiConfiguration), null);
			}).collect(Collectors.toList()));

		specification.setPaths(createPaths(tagLibrary));

		final Map<String, Object> schemaSection = createSchemaSection(tagLibrary);
		boolean schemaSectionCreated = false;
		if(!schemaSection.isEmpty()) {
			specification.getComponents().put("schemas", schemaSection);
			schemaSectionCreated = true;
		}

		if(freefields.isPresent() && freefields.get().get("components") != null) {

			final JsonNode componentsNode = freefields.get().get("components");
			if(componentsNode.get(OpenApiConstants.SCHEMAS) != null) {
				if(schemaSectionCreated) {
					// Adding elements to the schema section
					final Map<String, Object> createdSchema = (Map<String, Object>) specification.getComponents()
						.get(OpenApiConstants.SCHEMAS);

					final Iterator<Map.Entry<String, JsonNode>> iterator = componentsNode.get(OpenApiConstants.SCHEMAS).fields();
					iterator.forEachRemaining(entry -> {
						createdSchema.put(entry.getKey(), entry.getValue());
					});
				} else {
					// Creating the schemas section from scratch
					specification.getComponents().put(OpenApiConstants.SCHEMAS, componentsNode.get(OpenApiConstants.SCHEMAS));
				}
			}

			for(final String section : OpenApiConstants.COMPONENTS_STRUCTURE) {
				if(componentsNode.get(section) != null) {
					specification.getComponents().put(section, componentsNode.get(section));
				}
			}
		}

		om.writeValue(file, specification);
	}

	private Map<String, Map<String, Operation>> createPaths(final TagLibrary tagLibrary) {
		final Map<String, Map<String, Operation>> paths = new LinkedHashMap<>();

		final Set<String> operationIds = new HashSet<>();

		for(final Tag tag : tagLibrary.getTags()) {

			final ClassDocumentation classDocumentation = JavadocMap.INSTANCE.isPresent() ?
				JavadocMap.INSTANCE.getJavadocMap().get(tag.getClazz().getCanonicalName()) : null;

			logger.debug(
				"Class documentation found for tag paths section " + tag.getClazz().getSimpleName() + " ? " + (classDocumentation != null));

			// There is no need to try to enhance with the abstract or interfaces classes the documentation here.
			// It has already been made when we were writing the tags

			// List of operations, which will be sorted before storing them by path. In order to keep a deterministic generation.
			List<Operation> operations = new ArrayList<>();

			for (final Endpoint endpoint : tag.getEndpoints()) {

				final String enhancedPath = this.apiConfiguration.getPathPrefix() + endpoint.getPath();
				paths.computeIfAbsent(enhancedPath, k -> new LinkedHashMap<>());

				final Operation operation = new Operation();
				operations.add(operation);
				operation.setName(endpoint.getType().name());
				operation.setPath(enhancedPath);
				final String computedTagName = tag.computeConfiguredName(apiConfiguration);
				operation.getTags().add(computedTagName);
				operation.setOperationId(
					apiConfiguration.getOperationIdHelper().toOperationId(tag.getName(), computedTagName, endpoint.getName()));
				if(apiConfiguration.isLoopbackOperationName()) {
					operation.setLoopbackOperationName(endpoint.getName());
				}
				operation.setDeprecated(endpoint.isDeprecated());

				// Javadoc to description
				JavadocWrapper methodJavadoc = null;
				if(classDocumentation != null) {
					methodJavadoc = classDocumentation.getMethodsJavadoc().get(endpoint.getIdentifier());
					if(methodJavadoc != null) {
						methodJavadoc.sortTags();
						operation.setDescription(methodJavadoc.getJavadoc().getDescription().toText());
					}
					logger.debug(
						"Method documentation found for endpoint method " + endpoint.getIdentifier() + " ? " + (methodJavadoc != null));
				}

				// Warning on paths
				if(!operation.getPath().startsWith("/")) {
					Logger.INSTANCE.getLogger().warn("Operation " + operation.getOperationId()
						+ " path should start with a \"/\" (" + operation.getPath() + ")");
				}
				// Warning on operation Ids
				if(!operationIds.add(operation.getOperationId())) {
					Logger.INSTANCE.getLogger().warn("Operation id \"" +
						operation.getOperationId() + "\" (" + tag.getName() + ") should be unique");
				}

				// -------------------------
				// ----- PARAMETERS part----
				// -------------------------

				// All parameters which are not in the body
				for(final ParameterObject parameter : endpoint.getParameters().stream()
					.filter(x -> ParameterLocation.BODY != x.getLocation()).collect(Collectors.toList())) {
					final ParameterElement parameterElement = new ParameterElement();
					parameterElement.setName(parameter.getName());
					parameterElement.setIn(parameter.getLocation().toString().toLowerCase(Locale.ENGLISH));
					parameterElement.setRequired(parameter.isRequired());
					final Property schema = new Property(Content.fromDataObject(parameter).getSchema());

					// array in path parameters are not supported
					if(OpenApiDataType.ARRAY == parameter.getOpenApiType() && ParameterLocation.PATH == parameter.getLocation()) {
						logger.warn("Array types in path or query parameter are not allowed : "
							+ endpoint.getPath() + " - " + endpoint.getType());
					}
					parameterElement.setSchema(schema);

					// Javadoc handling
					if(methodJavadoc != null) {
						final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(parameterElement.getName());
						if(parameterDoc.isPresent()) {
							final String description = parameterDoc.get().getContent().toText();
							if(!description.isEmpty()) {
								parameterElement.setDescription(parameterDoc.get().getContent().toText());
							}
						}
						logger.debug(
							"Parameter documentation found for endpoint parameter " + parameterElement.getName() + " ? "
								+ parameterDoc.isPresent());
					}

					operation.getParameters().add(parameterElement);
				}

				// There can be only one body
				final List<ParameterObject> bodies = endpoint.getParameters().stream()
					.filter(x -> ParameterLocation.BODY == x.getLocation())
					.collect(Collectors.toList());
				if(bodies.size() > 1) {
					logger.warn("More than one body is not allowed : "
						+ endpoint.getPath() + " - " + endpoint.getType());
				}
				if(!bodies.isEmpty()) {
					final ParameterObject body = bodies.get(0);
					final RequestBody requestBody = new RequestBody();
					operation.setRequestBody(requestBody);
					final Content requestBodyContent = Content.fromDataObject(body);
					if(body.getFormats() != null) {
						for(final String format : body.getFormats()) {
							requestBody.getContent().put(format, requestBodyContent);
						}
					} else if(apiConfiguration.isDefaultProduceConsumeGuessing()) {
						requestBody.getContent().put(ProduceConsumeUtils.getDefaultValue(body), requestBodyContent);
					} else {
						requestBody.getContent().put("*/*", requestBodyContent);
					}

					// Javadoc handling
					if(methodJavadoc != null) {
						final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(body.getName());
						if(parameterDoc.isPresent()) {
							final String description = parameterDoc.get().getContent().toText();
							if(!description.isEmpty()) {
								requestBody.setDescription(parameterDoc.get().getContent().toText());
							}
						}
						logger.debug(
							"Parameter documentation found for endpoint body " + body.getName() + " ? "
								+ parameterDoc.isPresent());
					}

				}

				// -------------------------
				// ----- RESPONSE part----
				// -------------------------

				final Response response = new Response();
				response.setCode(endpoint.getResponseCode(), apiConfiguration.getDefaultSuccessfulOperationDescription());
				if(endpoint.getResponseObject() != null) {
					final Content responseContent = Content.fromDataObject(endpoint.getResponseObject());
					if(endpoint.getResponseFormats() != null) {
						for(final String format : endpoint.getResponseFormats()) {
							response.getContent().put(format, responseContent);
						}
					} else if(apiConfiguration.isDefaultProduceConsumeGuessing()) {
						response.getContent().put(ProduceConsumeUtils.getDefaultValue(endpoint.getResponseObject()), responseContent);
					} else {
						response.getContent().put("*/*", responseContent);
					}
				}

				// Javadoc handling
				if(methodJavadoc != null) {
					final Optional<JavadocBlockTag> returnDoc = methodJavadoc.getReturnBlockTag();
					if(returnDoc.isPresent()) {
						final String description = returnDoc.get().getContent().toText();
						if(!description.isEmpty()) {
							response.setDescription(returnDoc.get().getContent().toText());
						}
					}
					logger.debug(
						"Return documentation found ? " + returnDoc.isPresent());
				}

				operation.getResponses().put(response.getCode(), response);

				// Adding default responses
				if(defaultErrors != null) {
					defaultErrors.entrySet().forEach(entry -> {
						operation.getResponses().put(entry.getKey(), entry.getValue());
					});
				}

			}

			// We now order operations by types :
			operations = operations.stream().sorted(Comparator.comparing(Operation::getName)).collect(Collectors.toList());
			// And map them to their path
			for(final Operation operation : operations) {
				final Operation previousOperation = paths.get(operation.getPath()).put(operation.getName().toLowerCase(), operation);
				if(previousOperation != null) {
					throw new MojoRuntimeException(
						"More than one operation mapped on " + operation.getName() + " : " + operation.getPath() + " in tag "
							+ tag.getName());
				}
			}

		}
		return paths;
	}

	private Map<String, Object> createSchemaSection(final TagLibrary library) {
		final List<DataObject> ordered = library.getSchemaObjects().stream()
			.sorted(Comparator.comparing(p -> p.getSchemaReferenceName())).collect(Collectors.toList());

		// LinkedHashMap to keep alphabetical order
		final Map<String, Object> schemas = new LinkedHashMap<>();
		for(final DataObject dataObject : ordered) {
			final Set<String> exploredSignatures = new HashSet<>();
			final Schema schema = new Schema(dataObject, true, exploredSignatures, null, null);
			schemas.put(dataObject.getSchemaReferenceName(), schema);
		}
		// Add the additional eventual recursive entries.
		for(final Map.Entry<String, DataObject> entry : AdditionnalSchemaLibrary.getMap().entrySet()) {
			final Set<String> exploredSignatures = new HashSet<>();
			final Schema schema = new Schema(entry.getValue(), true, exploredSignatures, null, null);
			schemas.put(entry.getKey(), schema);
		}
		return schemas;
	}
}

