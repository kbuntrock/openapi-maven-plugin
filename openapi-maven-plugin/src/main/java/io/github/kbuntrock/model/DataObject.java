package io.github.kbuntrock.model;

import io.github.kbuntrock.reflection.GenericArrayTypeImpl;
import io.github.kbuntrock.reflection.ParameterizedTypeImpl;
import io.github.kbuntrock.reflection.ReflectionsUtils;
import io.github.kbuntrock.utils.OpenApiDataType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represent a type with all the needed informations to insert it into the openapi specification
 */
public class DataObject {

	/**
	 * Array of two elements in case of a map object :
	 * index 0 : the key type
	 * index 1 : the value type
	 */
	private final DataObject[] mapKeyValueDataObjects = new DataObject[2];
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
	private final OpenApiDataType openApiType;
	/**
	 * The type of the items if this data object represent a java Collection or java array
	 */
	private DataObject arrayItemDataObject;
	/**
	 * All the value's names if this data object represent a java enum
	 */
	private List<String> enumItemValues;
	/**
	 * True if this object is generically typed
	 */
	private boolean genericallyTyped;
	/**
	 * The type can be a parametrized type
	 */
	private Map<String, Type> genericNameToTypeMap;


	public DataObject(Type originalType) {
		Type type = originalType;
		try {
			if(type instanceof WildcardType) {
				// This block is in charge of handling the "? extends XX" syntax
				WildcardType wt = (WildcardType) originalType;
				if(wt.getLowerBounds().length == 0 && wt.getUpperBounds().length == 1) {
					type = wt.getUpperBounds()[0];
				}
			}

			this.javaType = type;
			if(type instanceof ParameterizedType) {
				// Parameterized types (List, Map, or every custom type)

				this.genericallyTyped = true;
				ParameterizedType pt = (ParameterizedType) type;
				javaClass = Class.forName(ReflectionsUtils.getClassNameFromType(pt.getRawType()),
					true, ReflectionsUtils.getProjectClassLoader());
				genericNameToTypeMap = new HashMap<>();
				for(int i = 0; i < pt.getActualTypeArguments().length; i++) {
					this.genericNameToTypeMap.put(javaClass.getTypeParameters()[i].getTypeName(),
						pt.getActualTypeArguments()[i]);
				}

				if(Collection.class.isAssignableFrom(javaClass)) {
					arrayItemDataObject = new DataObject(pt.getActualTypeArguments()[0]);
				} else if(Map.class.isAssignableFrom(javaClass)) {
					mapKeyValueDataObjects[0] = new DataObject(pt.getActualTypeArguments()[0]);
					mapKeyValueDataObjects[1] = new DataObject(pt.getActualTypeArguments()[1]);
				}

			} else if(type instanceof GenericArrayType) {

				// Parameterized array

				this.genericallyTyped = true;
				// See https://stackoverflow.com/questions/15450356/how-to-make-class-forname-return-array-type
				GenericArrayType gat = (GenericArrayType) type;
				if(gat.getGenericComponentType() instanceof ParameterizedType) {
					genericNameToTypeMap = new HashMap<>();
					ParameterizedType gpt = (ParameterizedType) gat.getGenericComponentType();
					javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(gpt.getRawType()) + ";",
						true, ReflectionsUtils.getProjectClassLoader());
					Class<?> rawJavaClass = Class.forName(ReflectionsUtils.getClassNameFromType(gpt.getRawType()),
						true, ReflectionsUtils.getProjectClassLoader());
					for(int i = 0; i < gpt.getActualTypeArguments().length; i++) {
						this.genericNameToTypeMap.put(rawJavaClass.getTypeParameters()[i].getTypeName(),
							gpt.getActualTypeArguments()[i]);
					}
					this.arrayItemDataObject = new DataObject(gpt);
				} else if(gat.getGenericComponentType() instanceof Class<?>) {
					Class<?> clazz = (Class<?>) gat.getGenericComponentType();
					javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(clazz) + ";",
						true, ReflectionsUtils.getProjectClassLoader());
					this.arrayItemDataObject = new DataObject(clazz);
				} else {
					throw new RuntimeException(
						"A GenericArrayType with a " + gat.getGenericComponentType().getClass().toString() + " is not and handled case.");
				}
			} else if(type instanceof Class) {

				// Anything simplier ...
				javaClass = (Class<?>) type;
			} else {
				throw new RuntimeException(
					"Type " + originalType.getTypeName() + " (+" + originalType.getClass().getSimpleName() + " is not supported yet");
			}

			this.openApiType = OpenApiDataType.fromJavaClass(javaClass);
			if(javaClass.isEnum()) {
				Object[] values = javaClass.getEnumConstants();
				this.enumItemValues = new ArrayList<>();
				for(Object value : values) {
					this.enumItemValues.add(value.toString());
				}

			} else if(javaClass.isArray() && !genericallyTyped) {
				arrayItemDataObject = new DataObject(javaClass.getComponentType());
			}

		} catch(ClassNotFoundException ex) {
			throw new RuntimeException("ClassNotFound wrapped", ex);
		}

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
		return isEnum() || (!genericallyTyped && OpenApiDataType.OBJECT == openApiType);
	}

	/**
	 * Generically typed object can not be written in the schema section. The have to be described in the content or response parts,
	 * as the depends from the context
	 *
	 * @return true if the object should be described in the content or reponse parts
	 */
	public boolean isGenericallyTypedObject() {
		return OpenApiDataType.OBJECT == openApiType && genericallyTyped;
	}

	/**
	 * @return true if the object is an array in the open api way
	 */
	public boolean isOpenApiArray() {
		return OpenApiDataType.ARRAY == openApiType;
	}

	public boolean isJavaArray() {
		return arrayItemDataObject != null && !genericallyTyped;
	}

	public OpenApiDataType getOpenApiType() {
		return openApiType;
	}

	public DataObject getArrayItemDataObject() {
		return arrayItemDataObject;
	}

	public List<String> getEnumItemValues() {
		return enumItemValues;
	}

	public DataObject getMapKeyType() {
		return mapKeyValueDataObjects[0];
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

	public String getSignature() {
		String genericJoin = genericNameToTypeMap == null ? "" : genericNameToTypeMap.values()
			.stream().map(v -> v.getTypeName()).collect(Collectors.joining("_"));
		String signature = javaClass.toGenericString() + "__" + genericJoin;
		return signature;
	}

	public String getSchemaRecursiveSuffix() {
		String genericJoin = genericNameToTypeMap == null ? "" : genericNameToTypeMap.values()
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
				TypeVariable typeVariable = (TypeVariable) genericType;
				if(this.getGenericNameToTypeMap().containsKey(typeVariable.getName())) {
					return this.getGenericNameToTypeMap().get(typeVariable.getName());
				}
			} else if(genericType instanceof ParameterizedType) {

				ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(((ParameterizedType) genericType));
				doContextualSubstitution(substitution);
				return substitution;

			} else if(genericType instanceof GenericArrayType) {
				GenericArrayType genericArrayType = (GenericArrayType) genericType;
				if(genericArrayType.getGenericComponentType() instanceof ParameterizedType) {
					ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(
						(ParameterizedType) genericArrayType.getGenericComponentType());
					doContextualSubstitution(substitution);
					GenericArrayType substitionArrayType = new GenericArrayTypeImpl(substitution);
					return substitionArrayType;
				} else if(genericArrayType.getGenericComponentType() instanceof TypeVariable<?>) {
					TypeVariable<?> typeVariable = (TypeVariable<?>) genericArrayType.getGenericComponentType();
					if(this.getGenericNameToTypeMap().containsKey(typeVariable.getName())) {
						GenericArrayType substitionArrayType = new GenericArrayTypeImpl(
							this.getGenericNameToTypeMap().get(typeVariable.getName()));
						return substitionArrayType;
					}
				} else {
					throw new RuntimeException("Type : " + ((GenericArrayType) genericType).getGenericComponentType().getClass().toString()
						+ " not handled in generic array contextual substitution.");
				}

			}
		}
		return genericType;
	}

	private void doContextualSubstitution(ParameterizedTypeImpl substitution) {
		for(int i = 0; i < substitution.getActualTypeArguments().length; i++) {
			if(this.getGenericNameToTypeMap().containsKey(substitution.getActualTypeArguments()[i].getTypeName())) {
				substitution.getActualTypeArguments()[i] =
					this.getGenericNameToTypeMap().get(substitution.getActualTypeArguments()[i].getTypeName());
			}
			substitution.getActualTypeArguments()[i] = getContextualType(substitution.getActualTypeArguments()[i]);
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}
		DataObject that = (DataObject) o;
		return Objects.equals(javaClass, that.javaClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(javaClass);
	}

	@Override
	public String toString() {
		return "DataObject{" +
			"openApiType=" + openApiType +
			", arrayItemDataObject=" + arrayItemDataObject +
			'}';
	}
}
