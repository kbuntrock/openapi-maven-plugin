package io.github.kbuntrock.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.javaparser.javadoc.JavadocBlockTag;
import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.TagLibrary;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.parser.CommonParserUtils;
import io.github.kbuntrock.configuration.parser.JsonParserUtils;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.javadoc.ClassDocumentation.EnhancementType;
import io.github.kbuntrock.javadoc.JavadocMap;
import io.github.kbuntrock.javadoc.JavadocWrapper;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.model.annotation.OperationResponse;
import io.github.kbuntrock.reflection.AdditionnalSchemaLibrary;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.ObjectsUtils;
import io.github.kbuntrock.utils.OpenApiConstants;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class YamlWriter {

	private static final String SERVERS_FIELD = "servers";
	private static final String SECURITY_FIELD = "security";
	private static final String EXTERNAL_DOC_FIELD = "externalDocs";
	private static final String FILEFORMAT_JSON = "json";
	private final Log logger = Logger.INSTANCE.getLogger();

	private final ObjectMapper om;
	private final ApiConfiguration apiConfiguration;
	private final OpenApiTypeResolver openApiTypeResolver;

	private final MavenProject mavenProject;

	private Optional<JsonNode> freefields = Optional.empty();
	private Map<String, JsonNode> defaultErrors;

	public YamlWriter(final MavenProject mavenProject, final ApiConfiguration apiConfiguration,
		final OpenApiTypeResolver openApiTypeResolver) {
		this.apiConfiguration = apiConfiguration;
		this.mavenProject = mavenProject;
		this.openApiTypeResolver = openApiTypeResolver;
		this.om = FILEFORMAT_JSON.equals(apiConfiguration.getFileFormat()) ?
			new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT) :
			new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
				.enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));
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

	private String computeFreeFields(final MavenProject mavenProject, final ApiConfiguration apiConfiguration) {
		if(apiConfiguration.isMergeFreeFields() && apiConfiguration.getBaseFreeField() != null) {
			final String baseContent = CommonParserUtils.getContentFromFileOrText(mavenProject,
				apiConfiguration.getBaseFreeField());
			final String mergingContent = CommonParserUtils.getContentFromFileOrText(mavenProject,
				apiConfiguration.getFreeFields());
			return JsonParserUtils.merge(baseContent, mergingContent);
		}

		return CommonParserUtils.getContentFromFileOrText(mavenProject, apiConfiguration.getFreeFields());
	}

	public void write(final File file, final TagLibrary tagLibrary) throws IOException {

		freefields = JsonParserUtils.parse(computeFreeFields(mavenProject, apiConfiguration));
		final Optional<JsonNode> defaultErrorsNode = JsonParserUtils.parse(
			CommonParserUtils.getContentFromFileOrText(mavenProject, apiConfiguration.getDefaultErrors()));
		if(defaultErrorsNode.isPresent()) {
			defaultErrors = new LinkedHashMap<>();
			final Iterator<Map.Entry<String, JsonNode>> iterator = defaultErrorsNode.get().fields();
			iterator.forEachRemaining(entry -> defaultErrors.put(entry.getKey(), entry.getValue()));
		}

		final Specification specification = new Specification();
		final Info info = new Info(mavenProject.getName(), mavenProject.getVersion(), freefields);
		specification.setInfo(info);

		populateSpecificationFreeFields(specification, freefields);

		// Write "tags" section (list of all tags presents in this documentation)
		specification.setTags(tagLibrary.getSortedTags().stream()
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

		// Write the "paths" section (all url / http verbs combinaison scanned)
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

		for(final Tag tag : tagLibrary.getSortedTags()) {

			final ClassDocumentation classDocumentation = JavadocMap.INSTANCE.isPresent() ?
				JavadocMap.INSTANCE.getJavadocMap().get(tag.getClazz().getCanonicalName()) : null;

			logger.debug(
				"Class documentation found for tag paths section " + tag.getClazz().getSimpleName() + " ? " + (classDocumentation != null));

			// There is no need to try to enhance with the abstract or interfaces classes the documentation here.
			// It has already been made when we were writing the tags

			// List of operations, already sorted via the Endpoint comparable implementation. In order to keep a deterministic generation.
			final List<Operation> operations = new ArrayList<>();

			for(final Endpoint endpoint : tag.getSortedEndpoints()) {

				final String enhancedPath = this.apiConfiguration.getPathPrefix() + endpoint.getPath();
				paths.computeIfAbsent(enhancedPath, k -> new LinkedHashMap<>());

				final Operation operation = new Operation();
				operations.add(operation);
				operation.setName(endpoint.getType().name());
				operation.setPath(enhancedPath);
				final String computedTagName = tag.computeConfiguredName(apiConfiguration);
				operation.getTags().add(computedTagName);
				operation.setOperationId(ObjectsUtils.nonNullElse(endpoint.getOperationAnnotationInfo().getOperationId(),
						apiConfiguration.getOperationIdHelper().toOperationId(tag.getName(), computedTagName, endpoint.getName()))
					);
				if(apiConfiguration.isLoopbackOperationName()) {
					operation.setLoopbackOperationName(endpoint.getName());
				}
				operation.setDeprecated(endpoint.isDeprecated());

				// Javadoc to description
				JavadocWrapper methodJavadoc = null;
				if(classDocumentation != null) {
					methodJavadoc = classDocumentation.getMethodsJavadocByIdentifier().get(endpoint.getIdentifier());
					if(methodJavadoc != null) {
						methodJavadoc.sortTags();
					}
					logger.debug(
						"Method documentation found for endpoint method " + endpoint.getIdentifier() + " ? " + (methodJavadoc != null));
				}

				if(endpoint.getOperationAnnotationInfo().getDescription() != null) {
					operation.setDescription(endpoint.getOperationAnnotationInfo().getDescription());
				} else {
					if(methodJavadoc != null) {
						operation.setDescription(methodJavadoc.getJavadoc().getDescription().toText());
					}
				}

				if(endpoint.getOperationAnnotationInfo().getSummary() != null) {
					operation.setSummary(endpoint.getOperationAnnotationInfo().getSummary());
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
					.filter(x -> ParameterLocation.BODY != x.getLocation() && ParameterLocation.BODY_PART != x.getLocation()).collect(Collectors.toList())) {
					final ParameterElement parameterElement = new ParameterElement();
					parameterElement.setName(parameter.getName());
					parameterElement.setIn(parameter.getLocation().toString().toLowerCase(Locale.ENGLISH));
					parameterElement.setRequired(parameter.isRequired());
					parameterElement.setAllowEmptyValue(parameter.isAllowEmptyValue());

					final Property schema = new Property(Content.fromDataObject(parameter).getSingleSchema());

					// array in path parameters are not supported
					if(OpenApiDataType.ARRAY == parameter.getOpenApiResolvedType().getType()
						&& ParameterLocation.PATH == parameter.getLocation()) {
						logger.warn("Array types in path or query parameter are not allowed : "
							+ endpoint.getPath() + " - " + endpoint.getType());
					}
					parameterElement.setSchema(schema);


					if(parameter.getJavaClass() == Object.class && parameter.isAllowEmptyValue() && parameter.getLocation() == ParameterLocation.QUERY) {
						parameterElement.setSchema(null);
					}

					// Javadoc handling
					if(methodJavadoc != null) {
						final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(
							parameter.getJavadocFieldName());
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
					// Case where parameter is extracted from a dto (see SpringMvcReader#bindDtoToQueryParams)
					if(parameter.getJavadocFieldClassName() != null) {
						final ClassDocumentation queryParamBindingClassDoc = JavadocMap.INSTANCE.isPresent() ?
							JavadocMap.INSTANCE.getJavadocMap().get(parameter.getJavadocFieldClassName()) : null;
						if(queryParamBindingClassDoc != null) {
							queryParamBindingClassDoc.inheritanceEnhancement(parameter.getJavaClass(), EnhancementType.BOTH);

							final JavadocWrapper javadocParamWrapper = queryParamBindingClassDoc.getFieldsJavadoc()
								.get(parameterElement.getName());
							if(javadocParamWrapper != null) {
								final Optional<String> desc = javadocParamWrapper.getDescription();
								parameterElement.setDescription(desc.get());
							}
						}
					}

					operation.getParameters().add(parameterElement);
				}

				// There can be only one body
				final List<ParameterObject> bodies = endpoint.getParameters().stream()
					.filter(x -> ParameterLocation.BODY == x.getLocation())
					.collect(Collectors.toList());
				if(bodies.size() > 1 && !isFormData(bodies)) {
					logger.warn("More than one body is not allowed : "
						+ endpoint.getPath() + " - " + endpoint.getType());
				}
				if(!bodies.isEmpty()) {
					final RequestBody requestBody = new RequestBody();
					operation.setRequestBody(requestBody);

					final ParameterObject body = bodies.get(0);
					final Content requestBodyContent = isFormData(bodies)
					 	? Content.fromMultipartBodies(bodies)
						: Content.fromDataObject(body);

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
						final Optional<JavadocBlockTag> parameterDoc = methodJavadoc.getParamBlockTagByName(body.getJavadocFieldName());
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

				List<ParameterObject> bodyParts = endpoint.getParameters().stream()
					.filter(p -> ParameterLocation.BODY_PART == p.getLocation()).collect(Collectors.toList());
				if(!bodyParts.isEmpty()) {
					if(operation.getRequestBody() != null) {
						logger.warn("Cannot handle \"body\" + \"body parts\" : "
							+ endpoint.getPath() + " - " + endpoint.getType());
					} else {
						final RequestBody requestBody = new RequestBody();
						operation.setRequestBody(requestBody);
						final Content requestBodyContent = Content.fromMultipartFormData(bodyParts, methodJavadoc);
						requestBody.getContent().put("multipart/form-data", requestBodyContent);
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

				// Add swagger documented responses
				for (final OperationResponse operationResponse : endpoint.getOperationAnnotationInfo().getResponses()) {
					// Check if response code is already documented, if so, we merge the attributes
					Response annotatedResponse = null;
					Object additionalResponseObject = operation.getResponses().get(operationResponse.getCode());
					if (additionalResponseObject != null && additionalResponseObject instanceof Response) {
						annotatedResponse = (Response) additionalResponseObject;
					} else {
						annotatedResponse = new Response();
					}

					annotatedResponse.setCode(operationResponse.getCode(), apiConfiguration.getDefaultSuccessfulOperationDescription());
					if (operationResponse.getDescription() != null) {
						annotatedResponse.setDescription(operationResponse.getDescription());
					}
					if (operationResponse.getDataObject() != null) {
						final Content responseContent = Content.fromDataObject(operationResponse.getDataObject());
						if (apiConfiguration.isDefaultProduceConsumeGuessing()) {
							annotatedResponse.getContent().put(ProduceConsumeUtils.getDefaultValue(operationResponse.getDataObject()), responseContent);
						} else {
							annotatedResponse.getContent().put("*/*", responseContent);
						}
					}
					operation.getResponses().put(annotatedResponse.getCode(), annotatedResponse);
				}

				// Check if on operation already exist for this name (GET / POST / ...) and path
				// If a similar operation exists, we could merge it if the return content type don't overlap.
				mergeCommonOperations(tag, paths, operation, response);
			}

		}
		return paths;
	}

	private static boolean isFormData(List<ParameterObject> bodies) {
		return bodies.stream().allMatch(ParameterObject::isMultipartFile);
	}

	/**
	 * Check if a common operation exist and merge it if possible
	 * @param tag the tag to document
	 * @param paths the already documented paths in this tag
	 * @param operation the current operation to document
	 * @param response the current operation response (without the default ones)
	 */
	private void mergeCommonOperations(Tag tag, Map<String, Map<String, Operation>> paths, Operation operation, Response response) {
		// Check if on operation already exist for this name (GET / POST / ...) and path
		Operation existingOperation = paths.get(operation.getPath()).get(operation.getName().toLowerCase());
		if(existingOperation == null) {
			paths.get(operation.getPath()).put(operation.getName().toLowerCase(), operation);
		} else {
			// Check if there is a collision in response content type.
			Object existingContent = existingOperation.getResponses().get(response.getCode());
			for(Entry<String, Content> responseContent : response.getContent().entrySet()) {
				if(existingContent instanceof Response ) {
					Response existingResponse = (Response) existingContent;
					if(existingResponse.getContent().containsKey(responseContent.getKey())) {
						// There are too many cases: this is uncommon, but it might be a valid case.
						logger.warn("More than one operation with a common content type mapped on " +
							operation.getName() + " : " + operation.getPath() + " in tag " + tag.getName());

						if(existingResponse.getContent().get(responseContent.getKey()).getSchemaList().stream()
							.noneMatch(x ->
								this.writeValueAsString(x.getJsonObject())
									.equals(this.writeValueAsString(responseContent.getValue().getSingleSchema()))
							)) {
							// Add response to the list of possibilities
							existingResponse.getContent().get(responseContent.getKey()).getSchemaList().add(responseContent.getValue()
								.getSingleSchema());
						}

					} else {
						// Operation merging is required (two functions, mapped on the same name and path, but with different return content type)
						existingResponse.getContent().put(responseContent.getKey(), responseContent.getValue());
					}
				}
			}
			// Now merging parameters
			Map<String, ParameterElement> existingParametersByNames = existingOperation.getParameters().stream().collect(Collectors.toMap(ParameterElement::getName, Function.identity()));
			for(ParameterElement parameter : operation.getParameters()) {
				ParameterElement existingParameter = existingParametersByNames.get(parameter.getName());
				if(existingParameter == null) {
					existingOperation.getParameters().add(parameter);
				} else {
					if(!existingParameter.getSchema().getType().getNode().toString().equals(parameter.getSchema().getType().getNode().toString())) {
						Logger.INSTANCE.getLogger().warn("Parameters incoherences detected in path " + operation.getPath());
					}
				}
			}
			// Please note that there is currently no verification on parameters being equivalent between similar response content types.
			// The first encountered operation's parameters are the one written in the documentation.
		}
	}

	private Map<String, Object> createSchemaSection(final TagLibrary library) {
		final List<DataObject> ordered = library.getSchemaObjects().stream()
			.sorted(Comparator.comparing(
				p -> p.getOpenApiResolvedType().isCompleteNode() ? p.getOpenApiResolvedType().getModelName() : p.getSchemaReferenceName()))
			.collect(Collectors.toList());

		// LinkedHashMap to keep alphabetical order
		final Map<String, Object> schemas = new LinkedHashMap<>();
		for(final DataObject dataObject : ordered) {
			final Set<String> exploredSignatures = new HashSet<>();
			final Schema schema = new Schema(dataObject, true, exploredSignatures, null, null);
			schemas.put(dataObject.getOpenApiResolvedType().isCompleteNode() ? dataObject.getOpenApiResolvedType().getModelName()
				: dataObject.getSchemaReferenceName(), schema);
		}
		// Add the additional eventual recursive entries.
		for(final Map.Entry<String, DataObject> entry : AdditionnalSchemaLibrary.getMap().entrySet()) {
			final Set<String> exploredSignatures = new HashSet<>();
			final Schema schema = new Schema(entry.getValue(), true, exploredSignatures, null, null);
			schemas.put(entry.getKey(), schema);
		}
		return schemas;
	}

	private String writeValueAsString(Object object) {
		try {
			return object == null ? null : this.om.writeValueAsString(object);
		} catch(JsonProcessingException e) {
			throw new MojoRuntimeException("Cannot write schema as json string", e);
		}
	}
}

