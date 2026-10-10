package io.github.kbuntrock.reflection;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class hierarchy described by {@code @JsonTypeInfo(use = NAME, include = PROPERTY)} and {@code @JsonSubTypes}. The sub-types and
 * their type ids are the ones Jackson resolves.
 */
public final class PolymorphicHierarchy {

	private final Class<?> root;
	private final String discriminatorProperty;
	private final Map<String, Class<?>> subTypes;

	private PolymorphicHierarchy(final Class<?> root, final String discriminatorProperty, final Map<String, Class<?>> subTypes) {
		this.root = root;
		this.discriminatorProperty = discriminatorProperty;
		this.subTypes = subTypes;
	}

	/**
	 * @param oneOf
	 *            true for the "oneOf" form, which cannot describe a root that can be instantiated
	 * @return the hierarchy the class belongs to, null if there is none
	 */
	public static PolymorphicHierarchy find(final Class<?> clazz, final ObjectMapper mapper, final boolean oneOf) {
		Class<?> root = null;
		JsonTypeInfo typeInfo = null;
		for(Class<?> current = clazz; current != null && !current.isInterface(); current = current.getSuperclass()) {
			final JsonTypeInfo currentTypeInfo = current.getAnnotation(JsonTypeInfo.class);
			if(currentTypeInfo != null && currentTypeInfo.use() == JsonTypeInfo.Id.NAME
				&& currentTypeInfo.include() == JsonTypeInfo.As.PROPERTY) {
				root = current;
				typeInfo = currentTypeInfo;
			}
		}
		if(root == null) {
			return null;
		}
		final DeserializationConfig config = mapper.getDeserializationConfig();
		final Map<String, Class<?>> subTypes = new TreeMap<>();
		for(final NamedType namedType : mapper.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config,
			config.introspectClassAnnotations(root).getClassInfo())) {
			final Class<?> subType = namedType.getType();
			if(!Modifier.isAbstract(subType.getModifiers()) && !subTypes.containsValue(subType)) {
				subTypes.put(namedType.hasName() ? namedType.getName() : getDefaultTypeId(subType), subType);
			}
		}
		if(subTypes.isEmpty() || (oneOf && subTypes.containsValue(root))) {
			return null;
		}
		return new PolymorphicHierarchy(root,
			typeInfo.property().isEmpty() ? typeInfo.use().getDefaultPropertyName() : typeInfo.property(), subTypes);
	}

	// Same default as Jackson: the class name without its package
	private static String getDefaultTypeId(final Class<?> clazz) {
		return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
	}

	public Class<?> getRoot() {
		return root;
	}

	public String getDiscriminatorProperty() {
		return discriminatorProperty;
	}

	/**
	 * @return the instantiable types of the hierarchy by type id, ordered by type id
	 */
	public Map<String, Class<?>> getSubTypes() {
		return subTypes;
	}
}
