package io.github.kbuntrock.model;

import com.google.common.reflect.TypeToken;
import io.github.kbuntrock.reflection.GenericArrayTypeImpl;
import io.github.kbuntrock.reflection.ParameterizedTypeImpl;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.Logger;
import io.github.kbuntrock.utils.OpenApiDataType;
import io.github.kbuntrock.utils.OpenApiResolvedType;
import io.github.kbuntrock.utils.OpenApiTypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.ReflectionUtils;

/**
 * Represent a type with all the needed informations to insert it into the openapi specification
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
	 * The original java class
	 */
	private final Class<?> javaClass;
	/**
	 * The original java type
	 */
	private final Type javaType;
	/**
	 * The corresponding openapi type
	 */
	private OpenApiResolvedType openApiResolvedType;
	/**
	 * The type of the items if this data object represent a java Collection or java array
	 */
	private DataObject arrayItemDataObject;
	/**
	 * True if this data object represent a java Set. Null if not relevant.
	 */
	private Boolean uniqueItems;
	/**
	 * All the value's names if this data object represent a java enum
	 */
	private List<String> enumItemValues;
	/**
	 * Used only if configured
	 */
	private List<String> enumItemNames;
	/**
	 * True if this object is generically typed
	 */
	private boolean genericallyTyped;
	/**
	 * The type can be a parametrized type
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
	 * Shallow copy for Parameter Object creation
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
				// Parameterized types (List, Map, or every custom type)

				this.genericallyTyped = true;
				final ParameterizedType pt = (ParameterizedType) type;
				javaClass = Class.forName(ReflectionsUtils.getClassNameFromType(pt.getRawType()),
					true, ReflectionsUtils.getProjectClassLoader());
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
						true, ReflectionsUtils.getProjectClassLoader());
					final Class<?> rawJavaClass = Class.forName(ReflectionsUtils.getClassNameFromType(gpt.getRawType()),
						true, ReflectionsUtils.getProjectClassLoader());
					for(int i = 0; i < gpt.getActualTypeArguments().length; i++) {
						this.genericNameToTypeMap.put(rawJavaClass.getTypeParameters()[i].getTypeName(),
							gpt.getActualTypeArguments()[i]);
					}
					this.arrayItemDataObject = new DataObject(gpt, openApiTypeResolver);
				} else if(gat.getGenericComponentType() instanceof Class<?>) {
					final Class<?> clazz = (Class<?>) gat.getGenericComponentType();
					javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(clazz) + ";",
						true, ReflectionsUtils.getProjectClassLoader());
					this.arrayItemDataObject = new DataObject(clazz, openApiTypeResolver);
				} else {
					throw new RuntimeException(
						"A GenericArrayType with a " + gat.getGenericComponentType().getClass() + " is not and handled case.");
				}
			} else if(type instanceof Class) {
				javaClass = (Class<?>) type;
				if(Map.class.isAssignableFrom((Class<?>) type)) {
					computeMapTypes(openApiTypeResolver);
				} else if(Collection.class.isAssignableFrom((Class<?>) type)) {
					computeCollectionType(openApiTypeResolver);
				}
			} else {
				throw new RuntimeException(
					"Type " + originalType.getTypeName() + " (+" + originalType.getClass().getSimpleName() + ") is not supported yet.");
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

		for(final Method method : javaClass.getMethods()) {
			if(method.getParameters().length == 0) {
				final MergedAnnotations mergedAnnotations = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
				MergedAnnotation<Annotation> jsonAsValue = mergedAnnotations.get(JACKSON_ANNOTATION_JSON_VALUE);
				if(jsonAsValue.isPresent()) {
					elementWithAnnotation.add(method.getName());
				}
			}
		}
		if(elementWithAnnotation.size() > 1) {
			Logger.INSTANCE.getLogger().warn("Problem with definition of ["+javaClass.getCanonicalName()
					+ "]: Multiple 'as-value' methods defined [" + elementWithAnnotation.stream().sorted().collect(Collectors.joining(",")) +"]");
		} else if(elementWithAnnotation.size() == 1) {
            try {
				this.enumItemNames = new ArrayList<>();
				final Method method = javaClass.getMethod(elementWithAnnotation.get(0));
				ReflectionUtils.makeAccessible(method);
				this.openApiResolvedType = openApiTypeResolver.resolveFromJavaClass(method.getReturnType(), false);
				for(final Object value : javaClass.getEnumConstants()) {
					this.enumItemNames.add(((Enum) value).name());
					this.enumItemValues.add(method.invoke(value).toString());
				}
				// Method has precedence over fields, we return here
				return;
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				Logger.INSTANCE.getLogger().error("Error while representing enumeration "+javaClass.getCanonicalName()+"#"+elementWithAnnotation.get(0)+"()", e);
            }
        }

		for(final Field field : javaClass.getDeclaredFields()) {
			final MergedAnnotations mergedAnnotations = MergedAnnotations.from(field, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
			MergedAnnotation<Annotation> jsonAsValue = mergedAnnotations.get(JACKSON_ANNOTATION_JSON_VALUE);
			if(jsonAsValue.isPresent()) {
				elementWithAnnotation.add(field.getName());
			}
		}
		if(elementWithAnnotation.size() > 1) {
			Logger.INSTANCE.getLogger().warn("Problem with definition of ["+javaClass.getCanonicalName()
					+ "]: Multiple 'as-value' fields defined [" + elementWithAnnotation.stream().sorted().collect(Collectors.joining(",")) +"]");
		} else if(elementWithAnnotation.size() == 1) {
			try {
				this.enumItemNames = new ArrayList<>();
				final Field field = javaClass.getDeclaredField(elementWithAnnotation.get(0));
				ReflectionUtils.makeAccessible(field);
				this.openApiResolvedType = openApiTypeResolver.resolveFromJavaClass(field.getType(), false);
				for(final Object value : javaClass.getEnumConstants()) {
					this.enumItemNames.add(((Enum) value).name());
					this.enumItemValues.add(field.get(value).toString());
				}
				return;
			} catch (NoSuchFieldException | IllegalAccessException e) {
				Logger.INSTANCE.getLogger().error("Error while representing enumeration "+javaClass.getCanonicalName()+"#"+elementWithAnnotation.get(0), e);
			}

        }
		// Classic way to fill the enumeration values, based on the name.
		for(final Object value : javaClass.getEnumConstants()) {
			this.enumItemValues.add(((Enum) value).name());
		}

	}

	private void computeMapTypes(final OpenApiTypeResolver openApiTypeResolver) {
		TypeToken token = TypeToken.of(javaType);
		TypeToken<Map> superType = token.getSupertype(Map.class);
		Type[] resolvedArguments = ((ParameterizedType) superType.getType()).getActualTypeArguments();
		mapKeyValueDataObjects[0] = new DataObject(resolvedArguments[0], openApiTypeResolver);
		mapKeyValueDataObjects[1] = new DataObject(resolvedArguments[1], openApiTypeResolver);
	}

	private void computeCollectionType(final OpenApiTypeResolver openApiTypeResolver) {
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
	 * @return true if the object should be considered as a "reference object", in order to get its own schema section
	 */
	public boolean isReferenceObject() {
		return !isMap() && (isEnum() || (!genericallyTyped && OpenApiDataType.OBJECT == openApiResolvedType.getType()));
	}

	/**
	 * Generically typed object can not be written in the schema section. The have to be described in the content or response parts,
	 * as the depends from the context
	 *
	 * @return true if the object should be described in the content or reponse parts
	 */
	public boolean isGenericallyTypedObject() {
		return OpenApiDataType.OBJECT == openApiResolvedType.getType() && genericallyTyped;
	}

	/**
	 * @return true if the object is an array in the open api way
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
		final String genericJoin = genericNameToTypeMap == null ? "" : genericNameToTypeMap.values()
			.stream().map(v -> v.getTypeName()).collect(Collectors.joining("_"));
		final String signature = javaClass.toGenericString() + "#" + genericJoin;
		return signature;
	}

	public String getSchemaRecursiveSuffix() {
		final String genericJoin = genericNameToTypeMap == null ? "" : genericNameToTypeMap.values()
			.stream().map(v -> {
				if(v instanceof Class) {
					return ((Class) v).getSimpleName();
				}
				return v.getTypeName();
			}).collect(Collectors.joining("_"));
		return genericJoin;
	}

	/**
	 * Get the type, or the parameterized contextual one if the default is a generic.
	 *
	 * @param genericType method.getGenericReturnType() or field.getGenericType()
	 * @return a type
	 */
	public Type getContextualType(final Type genericType) {

		if(this.isGenericallyTyped()) {
			// It is possible that we will not substitute anything. In that cas, the substitution parameterized type
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
					throw new RuntimeException("Type : " + ((GenericArrayType) genericType).getGenericComponentType().getClass().toString()
						+ " not handled in generic array contextual substitution. Scanned object is : " + this.getJavaClass().getName());
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
				substitution.getActualTypeArguments()[i] =
					this.getGenericNameToTypeMap().get(substitution.getActualTypeArguments()[i].getTypeName());
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
