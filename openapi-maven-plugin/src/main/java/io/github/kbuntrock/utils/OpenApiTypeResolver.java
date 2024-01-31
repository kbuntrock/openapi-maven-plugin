package io.github.kbuntrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.YamlParserUtils;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
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


	public void init(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		// Loading model definition
		initModel(mavenProject, apiConfig);
		// Loading associations
		initModelAssociation(mavenProject, apiConfig);
	}

	private void initModel(final MavenProject mavenProject, final ApiConfiguration apiConfig) {
		modelMap.clear();

		final JsonNode root = YamlParserUtils.readResourceFile("/openapi-model.yml");
		initModelFromNode(root);

		// Init now the possible overriding / additions by api
		if(apiConfig.getOpenapiModelsPath() != null) {
			final JsonNode customRoot = YamlParserUtils.readFile(
				mavenProject.getBasedir() + FileSystems.getDefault().getSeparator() + apiConfig.getOpenapiModelsPath());
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
		if(apiConfig.getModelsAssociationsPath() != null) {
			final JsonNode customRoot = YamlParserUtils.readFile(
				mavenProject.getBasedir() + FileSystems.getDefault().getSeparator() + apiConfig.getModelsAssociationsPath());
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

}
