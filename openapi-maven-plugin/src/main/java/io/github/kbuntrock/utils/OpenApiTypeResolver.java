package io.github.kbuntrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.parser.CommonParserUtils;
import io.github.kbuntrock.configuration.parser.YamlParserUtils;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.maven.project.MavenProject;

/**
 * @author Kévin Buntrock
 */
public enum OpenApiTypeResolver {
	INSTANCE;

	private static final String EQUALITY = "equality";
	private static final String ASSIGNABILITY = "assignability";
	private static final String OBJECT = "object";
	private static final String ARRAY = "array";
	private static final String ENUM = "enum";
	public static final String JAVA_UTIL_COLLECTION = "java.util.Collection";

	private final Map<String, OpenApiResolvedType> modelMap = new HashMap<>();

	/**
	 * Model map storing any partially resolved models (arrays, objects, enums)
	 */
	private final Map<String, OpenApiResolvedType> nonCompleteModelMap = new HashMap<>();
	private final Map<String, OpenApiResolvedType> equalityMap = new HashMap<>();
	private final Map<Class<?>, OpenApiResolvedType> assignabilityMap = new HashMap<>();

	/**
	 * Unwrapping section
	 */
	private final Map<Class<?>, UnwrappingEntry> responseUnwrappingMap = new HashMap<>();
	private final Map<Class<?>, UnwrappingEntry> parametersUnwrappingMap = new HashMap<>();
	private final Map<Class<?>, UnwrappingEntry> schemaUnwrappingMap = new HashMap<>();


	public void init(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		// Loading model definition
		initModel(mavenProject, apiConfig);
		// Loading associations
		initModelAssociation(mavenProject, apiConfig);
		// Loading unwrapping definitions
		initUnwrappingDefinitions(mavenProject, apiConfig);
	}

	private void initModel(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		modelMap.clear();

		final JsonNode root = YamlParserUtils.readResourceFile("/openapi-model.yml");
		initModelFromNode(root);

		// Init now the possible overriding / additions by api
		if(apiConfig.getOpenapiModels() != null) {
			final JsonNode customRoot = CommonParserUtils.parse(mavenProject, apiConfig.getOpenapiModels()).get();
			initModelFromNode(customRoot);
		}
	}

	private void initModelFromNode(final JsonNode root) {
		root.fields().forEachRemaining(entry -> {
			final JsonNode modelNode = entry.getValue();
			final OpenApiResolvedType type = new OpenApiResolvedType(OpenApiDataType.fromJsonNode(modelNode), modelNode, entry.getKey());
			modelMap.put(entry.getKey(), type);
		});
	}

	private void initModelAssociation(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		equalityMap.clear();
		assignabilityMap.clear();
		final JsonNode root = YamlParserUtils.readResourceFile("/model-association.yml");
		initModelAssociationForEquality(root);
		initModelAssociationForAssignability(root);

		// Init now the possible overriding / additions by api
		if(apiConfig.getModelsAssociations() != null) {
			final JsonNode customRoot = CommonParserUtils.parse(mavenProject, apiConfig.getModelsAssociations()).get();
			initModelAssociationForEquality(customRoot);
			initModelAssociationForAssignability(customRoot);
		}
	}


	private void initModelAssociationForEquality(final JsonNode root) {
		final JsonNode equalityNode = root.get(EQUALITY);
		if(equalityNode == null) {
			// A model association file does not require to define an equality section
			return;
		}

		equalityNode.fields().forEachRemaining(entry -> {
			if(entry.getValue() == null) {
				equalityMap.remove(entry.getKey());
			} else {
				final OpenApiResolvedType resolvedType = modelMap.get(entry.getValue().asText());
				if(resolvedType == null) {
					throw new RuntimeException(
						"There is no model definition to honor association : " + entry.getKey() + " -> " + entry.getValue().asText());
				}
				equalityMap.put(entry.getKey(), resolvedType);
			}

		});
	}

	private void initModelAssociationForAssignability(final JsonNode root) {
		final JsonNode rootNode = root.get(ASSIGNABILITY);
		if(rootNode == null) {
			// A model association file does not require to define an assignability section
			return;
		}
		final ClassLoader classLoader = ReflectionsUtils.getProjectClassLoader();

		rootNode.fields().forEachRemaining(entry -> {

			Class<?> clazz = null;
			try {
				clazz = classLoader.loadClass(entry.getKey());
			} catch(final ClassNotFoundException ex) {
				Logger.INSTANCE.getLogger().debug("Model class " + entry.getValue().asText() + " not found (could be normal)");
			}

			if(clazz != null) {
				if(entry.getValue() == null) {
					assignabilityMap.remove(clazz);
				} else {
					final OpenApiResolvedType resolvedType = modelMap.get(entry.getValue().asText());
					if(resolvedType == null) {
						throw new RuntimeException(
							"There is no model definition to honor association : " + entry.getKey() + " -> " + entry.getValue().asText());
					}
					assignabilityMap.put(clazz, resolvedType);
				}
			}
		});
	}

	public OpenApiResolvedType resolveFromJavaClass(final Class<?> clazz) {
		return resolveFromJavaClass(clazz, true);
	}

	public OpenApiResolvedType resolveFromJavaClass(final Class<?> clazz, final boolean completeVersion) {
		final String canonicalName = resolveCanonicalName(clazz);
		final OpenApiResolvedType resolvedType = equalityMap.get(canonicalName);
		if(resolvedType != null) {
			if(!completeVersion) {
				return nonCompleteModelMap.computeIfAbsent(canonicalName, k -> {
					final OpenApiResolvedType nonCompleteVersion = resolvedType.copy();
					nonCompleteVersion.setCompleteNode(false);
					return nonCompleteVersion;
				});
			}
			return resolvedType;
		}
		for(final Class<?> c : assignabilityMap.keySet()) {
			if(c.isAssignableFrom(clazz)) {
				final OpenApiResolvedType resolvedAssignability = assignabilityMap.get(c);
				if(!completeVersion || JAVA_UTIL_COLLECTION.equals(c.getCanonicalName())) {
					return nonCompleteModelMap.computeIfAbsent(canonicalName, k -> {
						final OpenApiResolvedType nonCompleteVersion = resolvedAssignability.copy();
						nonCompleteVersion.setCompleteNode(false);
						return nonCompleteVersion;
					});
				}
				return resolvedAssignability;
			}
		}
		if(clazz.isArray()) {
			return resolveNonCompleteModel(ARRAY);
		} else if(clazz.isEnum()) {
			return resolveNonCompleteModel(ENUM);
		}

		return resolveNonCompleteModel(OBJECT);
	}

	private OpenApiResolvedType resolveNonCompleteModel(final String key) {
		return nonCompleteModelMap.computeIfAbsent(key, k -> {
			final OpenApiResolvedType completeVersion = modelMap.get(k).copy();
			completeVersion.setCompleteNode(false);
			return completeVersion;
		});
	}

	/**
	 * Get the canonical name of a class with a small enhancement : it replace primitive class
	 * by the corresponding regular class
	 *
	 * @param clazz
	 * @return the canonical name
	 */
	private String resolveCanonicalName(final Class<?> clazz) {
		if(Boolean.TYPE == clazz) {
			return Boolean.class.getCanonicalName();
		} else if(Integer.TYPE == clazz) {
			return Integer.class.getCanonicalName();
		} else if(Long.TYPE == clazz) {
			return Long.class.getCanonicalName();
		} else if(Float.TYPE == clazz) {
			return Float.class.getCanonicalName();
		} else if(Double.TYPE == clazz) {
			return Double.class.getCanonicalName();
		} else if(Short.TYPE == clazz) {
			return Short.class.getCanonicalName();
		} else if(Character.TYPE == clazz) {
			return Character.class.getCanonicalName();
		} else if(Byte.TYPE == clazz) {
			return Byte.class.getCanonicalName();
		}
		return clazz.getCanonicalName();
	}

	private void initUnwrappingDefinitions(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		responseUnwrappingMap.clear();
		parametersUnwrappingMap.clear();
		schemaUnwrappingMap.clear();

		final ClassLoader classLoader = ReflectionsUtils.getProjectClassLoader();

		final JsonNode root = YamlParserUtils.readResourceFile("/unwrapping-configuration.yml");
		root.get("response").fields().forEachRemaining(entry -> {
			registerUnwrappingEntry(classLoader, entry, responseUnwrappingMap, true);
		});
		root.get("parameter").fields().forEachRemaining(entry -> {
			registerUnwrappingEntry(classLoader, entry, parametersUnwrappingMap, true);
		});
		root.get("schema").fields().forEachRemaining(entry -> {
			registerUnwrappingEntry(classLoader, entry, schemaUnwrappingMap, true);
		});
	}

	/**
	 * Register an unwrapping entry in the resolver
	 *
	 * @param classLoader
	 * @param entry
	 * @param unwrappingMap the map to add the entry
	 * @param debug         true for default plugin configuration, false for user configuration to explicitely point errors
	 */
	private void registerUnwrappingEntry(final ClassLoader classLoader, final Map.Entry<String, JsonNode> entry,
		final Map<Class<?>, UnwrappingEntry> unwrappingMap, final boolean debug) {
		try {
			final Class<?> clazz = classLoader.loadClass(entry.getKey());
			final UnwrappingEntry unwrappingEntry = new UnwrappingEntry(clazz);
			unwrappingEntry.setTypeName(entry.getValue().get("typeName").asText());
			final JsonNode requiredNode = entry.getValue().get("required");
			if(requiredNode != null) {
				unwrappingEntry.setRequired(Boolean.valueOf(requiredNode.asText()));
			}
			unwrappingMap.put(clazz, unwrappingEntry);
		} catch(final ClassNotFoundException e) {
			final String message = "Cannot load unwrapping class " + entry.getKey() + "(normal if associated with a non used library)";
			if(debug) {
				Logger.INSTANCE.getLogger().debug(message);
			} else {
				throw new RuntimeException(message, e);
			}
		}
	}

	public DataObject unwrapDataObject(final DataObject dataObject, final UnwrappingType type) {
		Map<Class<?>, UnwrappingEntry> unwrappingMap = schemaUnwrappingMap;
		if(UnwrappingType.RESPONSE == type) {
			unwrappingMap = responseUnwrappingMap;
		} else if(UnwrappingType.PARAMETER == type) {
			unwrappingMap = parametersUnwrappingMap;
		}
		return unwrapDataObject(dataObject, unwrappingMap);
	}

	private DataObject unwrapDataObject(final DataObject dataObject, final Map<Class<?>, UnwrappingEntry> unwrappingMap) {
		for(final Entry<Class<?>, UnwrappingEntry> entry : unwrappingMap.entrySet()) {
			if(entry.getKey().isAssignableFrom(dataObject.getJavaClass())) {
				final DataObject unwrapped = new DataObject(
					dataObject.getGenericNameToTypeMap().get(entry.getValue().getTypeName()));

				// Instrasic class requirement (ex: Optional) is carried from multiple unwrapping
				if(dataObject.getClassRequired() != null) {
					unwrapped.setClassRequired(dataObject.getClassRequired());
				} else if(entry.getValue().getRequired() != null) {
					unwrapped.setClassRequired(entry.getValue().getRequired());
				}
				return unwrapDataObject(unwrapped, unwrappingMap);
			}
		}
		return dataObject;
	}

}
