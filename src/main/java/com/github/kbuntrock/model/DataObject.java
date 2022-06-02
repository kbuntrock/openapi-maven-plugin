package com.github.kbuntrock.model;

import com.github.kbuntrock.reflection.ReflectionsUtils;
import com.github.kbuntrock.utils.OpenApiDataType;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Represent a type with all the needed informations to insert it into the openapi specification
 */
public class DataObject {

    /**
     * The original java class
     */
    private Class<?> javaClass;
    /**
     * The original java type
     */
    private Type javaType;
    /**
     * The corresponding openapi type
     */
    private OpenApiDataType openApiType;
    /**
     * The type of the items if this data object represent a java Collection or java array
     */
    private DataObject arrayItemDataObject;
    /**
     * All the value's names if this data object represent a java enum
     */
    private List<String> enumItemValues;
    /**
     * Array of two elements in case of a map object :
     * index 0 : the key type
     * index 1 : the value type
     */
    private final DataObject[] mapKeyValueDataObjects = new DataObject[2];
    /**
     * True if this object is generically typed
     */
    private boolean genericallyTyped;
    /**
     * The type can be a parametrized type
     */
    private Map<String, Type> genericNameToTypeMap;


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

    public DataObject(Type type) {
        try {
            this.javaType = type;
            if (type instanceof ParameterizedType) {
                // Parameterized types (List, Map, or every custom type)

                this.genericallyTyped = true;
                ParameterizedType pt = (ParameterizedType) type;
                javaClass = Class.forName(ReflectionsUtils.getClassNameFromType(pt.getRawType()),
                        true, ReflectionsUtils.getProjectClassLoader());
                genericNameToTypeMap = new HashMap<>();
                for (int i = 0; i < pt.getActualTypeArguments().length; i++) {
                    this.genericNameToTypeMap.put(javaClass.getTypeParameters()[i].getTypeName(),
                            pt.getActualTypeArguments()[i]);
                }

                if (Collection.class.isAssignableFrom(javaClass)) {
                    arrayItemDataObject = new DataObject(pt.getActualTypeArguments()[0]);
                } else if (Map.class.isAssignableFrom(javaClass)) {
                    mapKeyValueDataObjects[0] = new DataObject(pt.getActualTypeArguments()[0]);
                    mapKeyValueDataObjects[1] = new DataObject(pt.getActualTypeArguments()[1]);
                }

            } else if (type instanceof GenericArrayType) {

                // Parameterized array

                this.genericallyTyped = true;
                // See https://stackoverflow.com/questions/15450356/how-to-make-class-forname-return-array-type
                GenericArrayType gat = (GenericArrayType) type;
                if (gat.getGenericComponentType() instanceof ParameterizedType) {
                    genericNameToTypeMap = new HashMap<>();
                    ParameterizedType gpt = (ParameterizedType) gat.getGenericComponentType();
                    javaClass = Class.forName("[L" + ReflectionsUtils.getClassNameFromType(gpt.getRawType()) + ";",
                            true, ReflectionsUtils.getProjectClassLoader());
                    Class<?> rawJavaClass = Class.forName(ReflectionsUtils.getClassNameFromType(gpt.getRawType()),
                            true, ReflectionsUtils.getProjectClassLoader());
                    for (int i = 0; i < gpt.getActualTypeArguments().length; i++) {
                        this.genericNameToTypeMap.put(rawJavaClass.getTypeParameters()[i].getTypeName(),
                                gpt.getActualTypeArguments()[i]);
                    }
                    this.arrayItemDataObject = new DataObject(gpt);

                } else {
                    throw new RuntimeException("A GenericArrayType with a non ParameterizedType is not and handled case.");
                }
            } else if (type instanceof Class) {

                // Anything simplier ...
                javaClass = (Class<?>) type;
            } else {
                throw new RuntimeException("Type " + type.getTypeName() + " (+" + type.getClass().getSimpleName() + " is not supported yet");
            }

            this.openApiType = OpenApiDataType.fromJavaClass(javaClass);
            if (javaClass.isEnum()) {
                Object[] values = javaClass.getEnumConstants();
                this.enumItemValues = new ArrayList<>();
                for (Object value : values) {
                    this.enumItemValues.add(value.toString());
                }

            } else if (javaClass.isArray() && !genericallyTyped) {
                arrayItemDataObject = new DataObject(javaClass.getComponentType());
            }

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("ClassNotFound wrapped", ex);
        }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
