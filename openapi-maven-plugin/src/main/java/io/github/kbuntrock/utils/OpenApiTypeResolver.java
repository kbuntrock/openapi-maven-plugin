package io.github.kbuntrock.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.kbuntrock.configuration.YamlParserUtils;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Kévin Buntrock
 */
public enum OpenApiTypeResolver {
	INSTANCE;

	private static final String EQUALITY = "equality";
	private static final String ASSIGNABILITY = "assignability";
	private static final String OBJECT = "object";
	private static final String ARRAY = "array";
	private static final String STRING = "string";

	private Map<String, OpenApiResolvedType> model;
	private Map<String, OpenApiResolvedType> equalityMap;
	private Map<Class<?>, OpenApiResolvedType> assignabilityMap;


	public void init() {
		// Loading model definition
		initModel();
		// Loading associations

		assignabilityMap = new HashMap<>();
		final JsonNode root = YamlParserUtils.readResourceFile("/model-association.yml");
		initModelAssociationForEquality(root);
		initModelAssociationForAssignability(root);
	}

	private void initModel() {
		model = new HashMap<>();
		final JsonNode root = YamlParserUtils.readResourceFile("/openapi-model.yml");
		final Iterator<Entry<String, JsonNode>> iterator = root.fields();
		iterator.forEachRemaining(entry -> {
			final JsonNode modelNode = entry.getValue();
			final OpenApiResolvedType type = new OpenApiResolvedType(OpenApiDataType.fromJsonNode(modelNode), modelNode);
			model.put(entry.getKey(), type);
		});
	}

	private void initModelAssociationForEquality(final JsonNode root) {
		equalityMap = new HashMap<>();
		final JsonNode equalityNode = root.get(EQUALITY);
		final Iterator<Entry<String, JsonNode>> iteratorEquality = equalityNode.fields();
		iteratorEquality.forEachRemaining(entry -> {
			if(entry.getValue() == null) {
				equalityMap.remove(entry.getKey());
			} else {
				final OpenApiResolvedType resolvedType = model.get(entry.getValue().asText());
				if(resolvedType == null) {
					throw new RuntimeException(
						"There is no model definition to honor association : " + entry.getKey() + " -> " + entry.getValue().asText());
				}
				equalityMap.put(entry.getKey(), resolvedType);
			}

		});
	}

	private void initModelAssociationForAssignability(final JsonNode root) {
		assignabilityMap = new HashMap<>();
		final JsonNode rootNode = root.get(ASSIGNABILITY);
		final ClassLoader classLoader = ReflectionsUtils.getProjectClassLoader();

		final Iterator<Entry<String, JsonNode>> iteratorEquality = rootNode.fields();
		iteratorEquality.forEachRemaining(entry -> {

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
					final OpenApiResolvedType resolvedType = model.get(entry.getValue().asText());
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
		final String canonicalName = resolveCanonicalName(clazz);
		final OpenApiResolvedType resolvedType = equalityMap.get(canonicalName);
		if(resolvedType != null) {
			return resolvedType;
		}
		for(final Class<?> c : assignabilityMap.keySet()) {
			if(c.isAssignableFrom(clazz)) {
				return assignabilityMap.get(c);
			}
		}
		if(clazz.isArray()) {
			return model.get(ARRAY);
		} else if(clazz.isEnum()) {
			return model.get(STRING);
		}

		return model.get(OBJECT);
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
