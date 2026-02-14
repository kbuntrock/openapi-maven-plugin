package io.github.kbuntrock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.context.ApiContext;
import io.github.kbuntrock.javadoc.ClassDocumentation;
import io.github.kbuntrock.model.DataObject;
import io.github.kbuntrock.model.Endpoint;
import io.github.kbuntrock.model.ParameterObject;
import io.github.kbuntrock.model.Tag;
import io.github.kbuntrock.model.annotation.OperationResponse;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.OpenApiTypeResolver;
import io.github.kbuntrock.yaml.model.ChildObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central registry for discovered API {@link Tag}s and the associated model {@link DataObject}s
 * that must be included under OpenAPI components/schemas.
 * <p>
 * Responsibilities:
 * - Maintain the list of tags as they are discovered and eagerly explore their endpoints.
 * - Traverse response and parameter models to collect schema {@link DataObject}s, avoiding duplicates.
 * - Handle generic types, arrays, and interface getter-derived properties.
 * - Provide deterministic, human-friendly schema reference names and resolve name collisions.
 * <p>
 * Notes on traversal:
 * - Deduplication is based on a stable {@link DataObject#getSignature()} to prevent infinite recursion.
 * - Only "reference objects" (objects that should be emitted as named schemas) are added directly to the set.
 * - Generic containers are traversed to their context-aware type arguments before inspection.
 */
public class TagLibrary {

	public static final String METHOD_GET_PREFIX = "get";
	public static final int METHOD_GET_PREFIX_SIZE = METHOD_GET_PREFIX.length();
	public static final String METHOD_IS_PREFIX = "is";
	public static final int METHOD_IS_PREFIX_SIZE = METHOD_IS_PREFIX.length();

	/** Type resolver used when building {@link DataObject}s for schema exploration. */
	private final OpenApiTypeResolver openApiTypeResolver;
	/** Plugin execution context (logger, configuration access, class loader). */
	private final ApiContext context;
	/** Current API configuration. */
	private final ApiConfiguration apiConfiguration;
	/** Optional map of class canonical names to extracted javadoc metadata. */
	private Map<String, ClassDocumentation> javadocMap;

	/** All collected tags (controllers). */
	private final List<Tag> tags = new ArrayList<>();
	/** Set of schema objects destined for components/schemas. */
	private final Set<DataObject> schemaObjects = new HashSet<>();
	/** Guard set of visited signatures to avoid re-processing and cycles. */
	private final Set<String> exploredSignatures = new HashSet<>();
	/** Convenience index to look up schema object by its Java class. */
	final Map<Class, DataObject> classToSchemaObject = new HashMap<>();

	public TagLibrary(final ApiContext context, Map<String, ClassDocumentation> javadocMap) {
		this.openApiTypeResolver = context.getOpenApiTypeResolver();
		this.apiConfiguration = context.getApiConfiguration();
		this.javadocMap = javadocMap;
		this.context = context;
	}

	/**
	 * Register a discovered tag and immediately explore its endpoints to collect schema models.
	 *
	 * @param tag
	 *            the controller tag to register
	 */
	public void addTag(final Tag tag) {
		tags.add(tag);
		exploreTagObjects(tag);
	}

	/**
	 * Add an extra data object explicitly configured by the user so that it appears in the schema section,
	 * even if not directly referenced by any endpoint.
	 *
	 * @param clazz
	 */
	public void addExtraClass(final Class clazz) {
		final DataObject dataObject = new DataObject(clazz, openApiTypeResolver);
		exploreDataObject(dataObject);
	}

	/**
	 * Analyse all endpoints of a tag (aka a REST controller) to extract all objects which will be written
	 * in the schema section: parameters and responses.
	 *
	 * @param tag
	 *            a rest controller
	 */
	private void exploreTagObjects(final Tag tag) {
		for(final Endpoint endpoint : tag.getEndpoints()) {
			if(endpoint.getResponseObject() != null) {
				exploreDataObject(endpoint.getResponseObject());
			}

			for(final ParameterObject parameterObject : endpoint.getParameters()) {
				exploreDataObject(parameterObject);
			}

			for(final OperationResponse operationResponse : endpoint.getOperationAnnotationInfo().getResponses()) {
				if(operationResponse.getDataObject() != null) {
					// If the response has a data object, it represents a response body to document.
					exploreDataObject(operationResponse.getDataObject());
				}
			}
		}
	}

	/**
	 * Depth-first exploration of a {@link DataObject}, with cycle prevention and handling of references,
	 * generic containers, and Java arrays.
	 */
	private void exploreDataObject(final DataObject dataObject) {
		// Avoid revisiting the same logical object (prevents cycles and redundant work).
		if(!exploredSignatures.add(dataObject.getSignature())) {
			return;
		}
		// Reference objects are candidates for top-level schemas. Non-map references are then inspected for nested types.
		if(dataObject.isReferenceObject()) {
			if(schemaObjects.add(dataObject)) {
				if(!dataObject.isMap()) {
					inspectObject(dataObject);
				}
			}
		} else if(dataObject.isGenericallyTyped()) {
			// For generic containers, traverse their type arguments in context (e.g., List<Foo<T>>).
			if(dataObject.getGenericNameToTypeMap() != null) {
				for(final Map.Entry<String, Type> entry : dataObject.getGenericNameToTypeMap().entrySet()) {
					final DataObject genericObject = new DataObject(dataObject.getContextualType(entry.getValue()),
						openApiTypeResolver);
					exploreDataObject(genericObject);
				}
			}
			inspectObject(dataObject);
		} else if(dataObject.isJavaArray()) {
			// Traverse array item type.
			exploreDataObject(dataObject.getArrayItemDataObject());
		}
	}

	/**
	 * Inspect fields and accessor methods of a complex object to discover nested types that should
	 * be included in the schema section.
	 */
	private void inspectObject(final DataObject explored) {
		// Enums or already "complete" nodes do not require further traversal.
		if(explored.getJavaClass().isEnum() ||
			explored.getOpenApiResolvedType().isCompleteNode()) {
			return;
		}
		if(apiConfiguration.getLegacySchemaMarshallingRules() == true) {
			// To be removed in v1
			exploreWithLegacyAlrorithm(explored);
		} else {
			List<ChildObject> childProperties = getPropertyObjectsToDocument(explored);
			for(ChildObject child : childProperties) {
				exploreDataObject(child.getDataObject());
			}
		}
	}

	public List<ChildObject> getPropertyObjectsToDocument(DataObject explored) {
		List<BeanPropertyDefinition> propertyDefinitions = getPropertyDefinitions(context.getSchemaObjectMapper(),
			explored.getJavaClass());
		List<ChildObject> childObjects = new ArrayList<>();

		for(BeanPropertyDefinition propertyDefinition : propertyDefinitions) {
			Type genericType;
			if(propertyDefinition.hasField()) {
				genericType = propertyDefinition.getField().getAnnotated().getGenericType();
			} else if(propertyDefinition.hasGetter()) {
				genericType = propertyDefinition.getGetter().getAnnotated().getGenericReturnType();
			} else if(propertyDefinition.hasSetter()) {
				genericType = propertyDefinition.getSetter().getAnnotated().getGenericReturnType();
			} else {
				continue;
			}
			childObjects.add(new ChildObject(propertyDefinition,
				new DataObject(explored.getContextualType(genericType), openApiTypeResolver)));
		}
		return childObjects;
	}

	/**
	 * All collected tags (in insertion order).
	 */
	public Collection<Tag> getTags() {
		return tags;
	}

	/**
	 * Tags sorted using their natural ordering.
	 */
	public Collection<Tag> getSortedTags() {
		return tags.stream().sorted().collect(Collectors.toList());
	}

	/**
	 * All schema objects that will be emitted in the components/schemas section.
	 */
	public Set<DataObject> getSchemaObjects() {
		return schemaObjects;
	}

	/**
	 * Lookup map for quickly retrieving a {@link DataObject} from a Java class.
	 */
	public Map<Class, DataObject> getClassToSchemaObject() {
		return classToSchemaObject;
	}

	/**
	 * Compute short, human-friendly schema reference names for all {@link DataObject}s and ensure uniqueness.
	 * <p>
	 * Strategy:
	 * - Iterate schema objects in deterministic order (by canonical class name) for stable outputs.
	 * - Prefer simple class name; if already taken, append an incrementing suffix ("_1", "_2", ...).
	 * - Maintain an index for quick class-to-schema mapping.
	 */
	public void resolveSchemaReferenceNames() {
		// Collect all short names in the schema section
		final Set<String> referenceNames = new HashSet<>();
		// Deterministic order ensures stable naming (important for diffs and client generation).
		final List<DataObject> orderedSchemaObjects = schemaObjects.stream()
			.sorted(Comparator.comparing(o -> o.getJavaClass().getCanonicalName())).collect(Collectors.toList());

		for(final DataObject object : orderedSchemaObjects) {
			final String basicShortName = object.getJavaClass().getSimpleName();
			String shortName = basicShortName;
			if(!referenceNames.contains(shortName)) {
				object.setSchemaReferenceName(shortName);
				referenceNames.add(shortName);
				classToSchemaObject.put(object.getJavaClass(), object);
			} else {
				int i = 1;
				while(referenceNames.contains(shortName)) {
					shortName = basicShortName + "_" + i;
					i++;
				}
				object.setSchemaReferenceName(shortName);
				referenceNames.add(shortName);
				classToSchemaObject.put(object.getJavaClass(), object);
			}
		}
	}

	public OpenApiTypeResolver getOpenApiTypeResolver() {
		return openApiTypeResolver;
	}

	public ApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	public ApiContext getContext() {
		return context;
	}

	public boolean hasJavadocMap() {
		return javadocMap != null;
	}

	public Map<String, ClassDocumentation> getJavadocMap() {
		return javadocMap;
	}

	private List<BeanPropertyDefinition> getPropertyDefinitions(ObjectMapper mapper, Class<?> clazz) {
		SerializationConfig config = mapper.getSerializationConfig();
		JavaType type = config.constructType(clazz);
		BeanDescription beanDesc = config.introspect(type);
		return beanDesc.findProperties();
	}

	/**
	 * To be removed in v1
	 *
	 * @param explored
	 */
	@Deprecated
	private void exploreWithLegacyAlrorithm(DataObject explored) {
		final List<Field> fields = ReflectionsUtils.getAllNonStaticFields(new ArrayList<>(), explored.getJavaClass());
		for(final Field field : fields) {
			if(field.isAnnotationPresent(JsonIgnore.class)) {
				// Field is explicitly ignored; skip it from schema traversal.
				continue;
			}
			final DataObject dataObject = new DataObject(explored.getContextualType(field.getGenericType()), openApiTypeResolver);
			exploreDataObject(dataObject);
		}
		// When exploring interfaces, also consider bean-style getters (getX/isY) without parameters.
		if(explored.getJavaClass().isInterface()) {
			final Method[] methods = explored.getJavaClass().getMethods();
			for(final Method method : methods) {

				if(method.getParameters().length == 0
					&& ((method.getName().startsWith(METHOD_GET_PREFIX) && method.getName().length() != METHOD_GET_PREFIX_SIZE) ||
						(method.getName().startsWith(METHOD_IS_PREFIX)) && method.getName().length() != METHOD_IS_PREFIX_SIZE)) {
					final DataObject dataObject = new DataObject(explored.getContextualType(method.getGenericReturnType()),
						openApiTypeResolver);
					exploreDataObject(dataObject);
				}
			}
		}
	}
}
