package io.github.kbuntrock.model;

import com.google.common.reflect.TypeToken;
import io.github.kbuntrock.reflection.GenericArrayTypeImpl;
import io.github.kbuntrock.reflection.ParameterizedTypeImpl;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.OpenApiResolvedType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Java type together with the contextual information required to translate it
 * into an OpenAPI schema node.
 * <p>
 * Responsibilities:
 * - Capture the underlying Java {@link Type} and {@link Class} and resolve the {@link OpenApiResolvedType}.
 * - Track generic type parameters and provide contextual substitution for nested types.
 * - Model container relationships (arrays, collections, maps) via {@link #arrayItemDataObject} and {@link #mapKeyValueDataObjects}.
 * - Handle enums, including Jackson {@code @JsonValue}-style representations for enum values.
 * - Provide utilities to determine whether a type should be emitted as a named schema (reference object).
 * <p>
 * Notes:
 * - Deduplication is done upstream using {@link #getSignature()} which encodes class + generic arguments.
 * - {@link #isReferenceObject()} helps decide whether this object should appear under components/schemas.
 * - {@link #getContextualType(Type)} applies generic substitutions using the current context.
 */
public class DataObject {

	private static final String JACKSON_ANNOTATION_JSON_VALUE = "com.fasterxml.jackson.annotation.JsonValue";
	/**
	 * Array of two elements in case of a map object :
	 * index 0 : the key type
	 * index 1 : the value type
	 */
	private DataObject[] mapKeyValueDataObjects = new DataObject[2];
	/**
	 * The resolved raw Java class for {@link #javaType} (or Object.class as a fallback).
	 */
	private final Class<?> javaClass;
	/**
	 * The original Java {@link Type} used to build this data object.
	 */
	private final Type javaType;
	/**
	 * The corresponding OpenAPI resolved type (primitive/object/array/map/etc.).
	 */
	private OpenApiResolvedType openApiResolvedType;
	/**
	 * The item type when this object represents a Java Collection or a Java array.
	 */
	private DataObject arrayItemDataObject;
	/**
	 * True if this data object represents a Java Set. Null if not relevant (non-container).
	 */
	private Boolean uniqueItems;
	/**
	 * The list of enum values (as strings) when this type is an enum. May be driven by @JsonValue.
	 */
	private List<String> enumItemValues;
	/**
	 * The list of enum constant names when @JsonValue is used for values (optional).
	 */
	private List<String> enumItemNames;
	/**
	 * True when this object is generically typed (e.g., List<T>, Map<K,V>, Optional<Foo>, T[], etc.).
	 */
	private boolean genericallyTyped;
	/**
	 * Mapping from generic parameter name (e.g., "T") to the contextual {@link Type} substituted here.
	 */
	private Map<String, Type> genericNameToTypeMap;

	/**
	 * The name to use in the schema section. Is only set when needed.
	 */
	private String schemaReferenceName;

	/**
	 * Sometimes a class intrinsically carry the information of its requirement (ex : optional).
	 * This attribute is only used in this context (not linked with potential annotations)
	 * A null value is expected if the class does not carry this information.
	 */
	private Boolean classRequired;

	/**
	 * Shallow copy for parameter/response object creation. Copies type identity and resolution data.
	 *
	 * @param dataObject
	 */
	public DataObject(final DataObject dataObject) {
		this.mapKeyValueDataObjects = dataObject.mapKeyValueDataObjects;
		this.javaClass = dataObject.javaClass;
		this.javaType = dataObject.javaType;
		this.openApiResolvedType = dataObject.openApiResolvedType;
		this.arrayItemDataObject = dataObject.arrayItemDataObject;
		this.enumItemValues = dataObject.enumItemValues;
		this.enumItemNames = dataObject.enumItemNames;
		this.genericallyTyped = dataObject.genericallyTyped;
		this.genericNameToTypeMap = dataObject.genericNameToTypeMap;
		this.schemaReferenceName = dataObject.schemaReferenceName;
		this.classRequired = dataObject.classRequired;
	}

	/**
	 * Build a {@link DataObject} from a Java {@link Type}, resolving container shapes, generics,
	 * and enums. This constructor may recursively instantiate nested {@link DataObject}s (e.g., array items).
	 *
	 * @param originalType
	 *            input Java type (class, parameterized, generic array, wildcard, etc.)
	 * @param openApiTypeResolver
	 *            resolver used to compute {@link OpenApiResolvedType}
	 */
	public DataObject(final Type originalType, final OpenApiTypeResolver openApiTypeResolver) {
		Type type = originalType;

		try {
			if(type instanceof WildcardType) {
				// This block is in charge of handling the "? extends XX" syntax
				final WildcardType wt = (WildcardType) originalType;
				if(wt.getLowerBounds().length == 0 && wt.getUpperBounds().length == 1) {
					type = wt.getUpperBounds()[0];
				}
			}

			this.javaType = type;
			if(type instanceof ParameterizedType) {
				// Parameterized types (List, Map, Optional, or any custom parameterized type).

				this.genericallyTyped = true;
				final ParameterizedType pt = (ParameterizedType) type;
				javaClass = Class.forName(ReflectionsUtils.getClassNameFromType(pt.getRawType()),
					true, openApiTypeResolver.getContext().getClassLoader());
				genericNameToTypeMap = new HashMap<>();
				for(int i = 0; i < pt.getActualTypeArguments().length; i++) {
					this.genericNameToTypeMap.put(javaClass.getTypeParameters()[i].getTypeName(),
						pt.getActualTypeArguments()[i]);
				}

				if(Map.class.isAssignableFrom(javaClass)) {
					computeMapTypes(openApiTypeResolver);
				} else if(Collection.class.isAssignableFrom(javaClass)) {
					computeCollectionType(openApiTypeResolver);
				}

			} else if(type instanceof GenericArrayType) {

				// Parameterized array
				this.genericallyTyped = true;
				// See https://stackoverflow.com/questions/15450356/how-to-make-class-forname-return-array-type
				final GenericArrayType gat = (GenericArrayType) type;
				if(gat.getGenericComponentType() instanceof ParameterizedType) {
					genericNameToTypeMap = new HashMap<>();
					final ParameterizedType gpt = (ParameterizedType) gat.getGenericComponentType();
					javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(gpt.getRawType()) + ";",
						true, openApiTypeResolver.getContext().getClassLoader());
					final Class<?> rawJavaClass = Class.forName(ReflectionsUtils.getClassNameFromType(gpt.getRawType()),
						true, openApiTypeResolver.getContext().getClassLoader());
					for(int i = 0; i < gpt.getActualTypeArguments().length; i++) {
						this.genericNameToTypeMap.put(rawJavaClass.getTypeParameters()[i].getTypeName(),
							gpt.getActualTypeArguments()[i]);
					}
					this.arrayItemDataObject = new DataObject(gpt, openApiTypeResolver);
				} else if(gat.getGenericComponentType() instanceof Class<?>) {
					final Class<?> clazz = (Class<?>) gat.getGenericComponentType();
					javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(clazz) + ";",
						true, openApiTypeResolver.getContext().getClassLoader());
					this.arrayItemDataObject = new DataObject(clazz, openApiTypeResolver);
				} else {
					javaClass = Object.class;
				}
			} else if(type instanceof Class) {
				javaClass = (Class<?>) type;
				if(Map.class.isAssignableFrom((Class<?>) type)) {
					computeMapTypes(openApiTypeResolver);
				} else if(Collection.class.isAssignableFrom((Class<?>) type)) {
					computeCollectionType(openApiTypeResolver);
				}
			} else {
				javaClass = Object.class;
			}

			this.openApiResolvedType = openApiTypeResolver.resolveFromJavaClass(javaClass);
			if(javaClass.isEnum()) {
				computeEnum(openApiTypeResolver);
			} else if(javaClass.isArray() && !genericallyTyped && javaClass != byte[].class) {
				arrayItemDataObject = new DataObject(javaClass.getComponentType(), openApiTypeResolver);
			}

			if(Set.class.isAssignableFrom(javaClass)) {
				uniqueItems = true;
			}

		} catch(final ClassNotFoundException ex) {
			throw new RuntimeException("ClassNotFound wrapped", ex);
		}

	}

	private void computeEnum(final OpenApiTypeResolver openApiTypeResolver) {

		this.enumItemValues = new ArrayList<>();
		List<String> elementWithAnnotation = new ArrayList<>();

		// Prefer an enum "as-value" representation declared via a no-arg method annotated with @JsonValue.
		for(final Method method : javaClass.getMethods()) {
			if(method.getParameters().length == 0) {
				final MergedAnnotations mergedAnnotations = openApiTypeResolver.getContext().getMergeAnnotationsHelper()
					.from(method);
				MergedAnnotation jsonAsValue = mergedAnnotations.get(JACKSON_ANNOTATION_JSON_VALUE);
				if(jsonAsValue.isPresent()) {
					elementWithAnnotation.add(method.getName());
				}
			}
		}
		if(elementWithAnnotation.size() > 1) {
			openApiTypeResolver.getContext().getLogger().warn("Problem with definition of [" + javaClass.getCanonicalName()
				+ "]: Multiple 'as-value' methods defined ["
				+ elementWithAnnotation.stream().sorted().collect(Collectors.joining(",")) + "]");
		} else if(elementWithAnnotation.size() == 1) {
			try {
				this.enumItemNames = new ArrayList<>();
				final Method method = javaClass.getMethod(elementWithAnnotation.get(0));
				ReflectionsUtils.makeAccessible(method);
				this.openApiResolvedType = openApiTypeResolver.resolveFromJavaClass(method.getReturnType(), false);
				for(final Object value : javaClass.getEnumConstants()) {
					this.enumItemNames.add(((Enum) value).name());
					this.enumItemValues.add(method.invoke(value).toString());
				}
				// Method has precedence over fields, we return here
				return;
			} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				openApiTypeResolver.getContext().getLogger().error("Error while representing enumeration "
					+ javaClass.getCanonicalName() + "#" + elementWithAnnotation.get(0) + "()", e);
			}
		}

		// If no method-level @JsonValue is found, check fields with @JsonValue.
		for(final Field field : javaClass.getDeclaredFields()) {
			final MergedAnnotations mergedAnnotations = openApiTypeResolver.getContext().getMergeAnnotationsHelper().from(field);
			MergedAnnotation jsonAsValue = mergedAnnotations.get(JACKSON_ANNOTATION_JSON_VALUE);
			if(jsonAsValue.isPresent()) {
				elementWithAnnotation.add(field.getName());
			}
		}
		if(elementWithAnnotation.size() > 1) {
			openApiTypeResolver.getContext().getLogger().warn("Problem with definition of [" + javaClass.getCanonicalName()
				+ "]: Multiple 'as-value' fields defined ["
				+ elementWithAnnotation.stream().sorted().collect(Collectors.joining(",")) + "]");
		} else if(elementWithAnnotation.size() == 1) {
			try {
				this.enumItemNames = new ArrayList<>();
				final Field field = javaClass.getDeclaredField(elementWithAnnotation.get(0));
				ReflectionsUtils.makeAccessible(field);
				this.openApiResolvedType = openApiTypeResolver.resolveFromJavaClass(field.getType(), false);
				for(final Object value : javaClass.getEnumConstants()) {
					this.enumItemNames.add(((Enum) value).name());
					this.enumItemValues.add(field.get(value).toString());
				}
				return;
			} catch(NoSuchFieldException | IllegalAccessException e) {
				openApiTypeResolver.getContext().getLogger().error(
					"Error while representing enumeration " + javaClass.getCanonicalName() + "#" + elementWithAnnotation.get(0),
					e);
			}

		}
		// Classic way to fill the enumeration values, based on the name.
		for(final Object value : javaClass.getEnumConstants()) {
			this.enumItemValues.add(((Enum) value).name());
		}

	}

	private void computeMapTypes(final OpenApiTypeResolver openApiTypeResolver) {
		// Use Guava TypeToken to recover generic type arguments erased at runtime for Map.
		TypeToken token = TypeToken.of(javaType);
		TypeToken<Map> superType = token.getSupertype(Map.class);
		Type[] resolvedArguments = ((ParameterizedType) superType.getType()).getActualTypeArguments();
		mapKeyValueDataObjects[0] = new DataObject(resolvedArguments[0], openApiTypeResolver);
		mapKeyValueDataObjects[1] = new DataObject(resolvedArguments[1], openApiTypeResolver);
	}

	private void computeCollectionType(final OpenApiTypeResolver openApiTypeResolver) {
		// Use Guava TypeToken to recover the element type erased at runtime for Collection.
		TypeToken token = TypeToken.of(javaType);
		TypeToken<Map> superType = token.getSupertype(Collection.class);
		Type[] resolvedArguments = ((ParameterizedType) superType.getType()).getActualTypeArguments();
		arrayItemDataObject = new DataObject(resolvedArguments[0], openApiTypeResolver);
	}

	/**
	 * @return true if this DataObject is a map
	 */
	public boolean isMap() {
		return mapKeyValueDataObjects[0] != null;
	}

	/**
	 * @return true if this DataObject is an enum
	 */
	public boolean isEnum() {
		return javaClass.isEnum();
	}

	/**
	 * @return true if the object should be considered a "reference object" and get its own schema entry
	 */
	public boolean isReferenceObject() {
		return !isMap() && (isEnum() || (!genericallyTyped && OpenApiDataType.OBJECT == openApiResolvedType.getType()));
	}

	/**
	 * Generically typed objects cannot be emitted directly in the schema section; they should be expanded
	 * in the content/response sections where their type arguments are known.
	 *
	 * @return true if the object must be described inline (content/response), not as a named schema
	 */
	public boolean isGenericallyTypedObject() {
		return OpenApiDataType.OBJECT == openApiResolvedType.getType() && genericallyTyped;
	}

	/**
	 * @return true if the object is an array from an OpenAPI perspective
	 */
	public boolean isOpenApiArray() {
		return OpenApiDataType.ARRAY == openApiResolvedType.getType();
	}

	public boolean isJavaArray() {
		return arrayItemDataObject != null && !genericallyTyped;
	}

	public Boolean getUniqueItems() {
		return uniqueItems;
	}

	public OpenApiResolvedType getOpenApiResolvedType() {
		return openApiResolvedType;
	}

	public DataObject getArrayItemDataObject() {
		return arrayItemDataObject;
	}

	public List<String> getEnumItemValues() {
		return enumItemValues;
	}

	public List<String> getEnumItemNames() {
		return enumItemNames;
	}

	public DataObject getMapValueType() {
		return mapKeyValueDataObjects[1];
	}

	public boolean isGenericallyTyped() {
		return genericallyTyped;
	}

	public Map<String, Type> getGenericNameToTypeMap() {
		return genericNameToTypeMap;
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	public Type getJavaType() {
		return javaType;
	}

	public Boolean getClassRequired() {
		return classRequired;
	}

	public void setClassRequired(final Boolean classRequired) {
		this.classRequired = classRequired;
	}

	public String getSignature() {
		// Encode class identity together with contextualized generic arguments to form a stable signature.
		final String genericJoin = genericNameToTypeMap == null ? ""
			: genericNameToTypeMap.values()
				.stream().map(v -> v.getTypeName()).collect(Collectors.joining("_"));
		final String signature = javaClass.toGenericString() + "#" + genericJoin;
		return signature;
	}

	public String getSchemaRecursiveSuffix() {
		// A compact suffix that reflects generic arguments when generating recursive schema names.
		final String genericJoin = genericNameToTypeMap == null ? ""
			: genericNameToTypeMap.values()
				.stream().map(v -> {
					if(v instanceof Class) {
						return ((Class) v).getSimpleName();
					}
					return v.getTypeName();
				}).collect(Collectors.joining("_"));
		return genericJoin;
	}

	/**
	 * Resolve a nested type using this object's generic context, returning a potentially substituted type.
	 * For example, for {@code class Box<T>} and {@code Box<String>}, calling with {@code T} returns {@code String}.
	 *
	 * @param genericType
	 *            method.getGenericReturnType() or field.getGenericType()
	 * @return a type
	 */
	public Type getContextualType(final Type genericType) {

		if(this.isGenericallyTyped()) {
			// It is possible that we will not substitute anything. In that case, the substitution parameterized type
			// will be equivalent to the source one.
			if(genericType instanceof TypeVariable) {
				final TypeVariable typeVariable = (TypeVariable) genericType;
				if(this.getGenericNameToTypeMap().containsKey(typeVariable.getName())) {
					return this.getGenericNameToTypeMap().get(typeVariable.getName());
				}
			} else if(genericType instanceof ParameterizedType) {

				final ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(((ParameterizedType) genericType));
				doContextualSubstitution(substitution);
				return substitution;

			} else if(genericType instanceof GenericArrayType) {
				final GenericArrayType genericArrayType = (GenericArrayType) genericType;
				if(genericArrayType.getGenericComponentType() instanceof ParameterizedType) {
					final ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(
						(ParameterizedType) genericArrayType.getGenericComponentType());
					doContextualSubstitution(substitution);
					final GenericArrayType substitionArrayType = new GenericArrayTypeImpl(substitution);
					return substitionArrayType;
				} else if(genericArrayType.getGenericComponentType() instanceof TypeVariable<?>) {
					final TypeVariable<?> typeVariable = (TypeVariable<?>) genericArrayType.getGenericComponentType();
					if(this.getGenericNameToTypeMap().containsKey(typeVariable.getName())) {
						final GenericArrayType substitionArrayType = new GenericArrayTypeImpl(
							this.getGenericNameToTypeMap().get(typeVariable.getName()));
						return substitionArrayType;
					}
				} else {
					throw new RuntimeException(
						"Type : " + ((GenericArrayType) genericType).getGenericComponentType().getClass().toString()
							+ " not handled in generic array contextual substitution. Scanned object is : "
							+ this.getJavaClass().getName());
				}

			}
		}
		// A not generic object type does not mean we are free from genericity ...
		if(genericType instanceof ParameterizedType) {
			// Here we handle "Class<? extends XXX> which can not be substituted locally.
			final ParameterizedType parameterizedType = (ParameterizedType) genericType;
			if(parameterizedType.getRawType() == Class.class && parameterizedType.getActualTypeArguments().length == 1
				&& parameterizedType.getActualTypeArguments()[0] instanceof WildcardType) {
				final WildcardType wt = (WildcardType) parameterizedType.getActualTypeArguments()[0];
				if(wt.getLowerBounds().length == 0 && wt.getUpperBounds().length == 1) {
					// Return "XXX" as the only type we can determine for this object.
					// The implementation surely will be a child of this type but we can't guess it.
					return wt.getUpperBounds()[0];
				}
			} else {
				// Here we handle local extends with genericity (extends XXX<YYY>)
				return TypeToken.of(this.getJavaClass()).resolveType(genericType).getType();
			}
		} else if(genericType instanceof TypeVariable) {

			// We are in presence of a generic type variable not coming from the outside. Might be coming from generic typing at a parent level.
			return TypeToken.of(this.getJavaClass()).resolveType(genericType).getType();
		}

		return genericType;
	}

	private void doContextualSubstitution(final ParameterizedTypeImpl substitution) {
		for(int i = 0; i < substitution.getActualTypeArguments().length; i++) {
			if(this.getGenericNameToTypeMap().containsKey(substitution.getActualTypeArguments()[i].getTypeName())) {
				substitution.getActualTypeArguments()[i] = this.getGenericNameToTypeMap()
					.get(substitution.getActualTypeArguments()[i].getTypeName());
			}
			substitution.getActualTypeArguments()[i] = getContextualType(substitution.getActualTypeArguments()[i]);
		}
	}

	public String getSchemaReferenceName() {
		return schemaReferenceName;
	}

	public void setSchemaReferenceName(final String schemaReferenceName) {
		this.schemaReferenceName = schemaReferenceName;
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		final DataObject that = (DataObject) o;
		return Objects.equals(javaClass, that.javaClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(javaClass);
	}

	@Override
	public String toString() {
		return "DataObject{" +
			"openApiType=" + openApiResolvedType +
			", arrayItemDataObject=" + arrayItemDataObject +
			'}';
	}
}
