package io.github.kbuntrock.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to resolve genericity on classes which are DataObjects. (usually endpoints)
 *
 * @author Kévin Buntrock
 */
public class ClassGenericityResolver {

	private final Class<?> clazz;

	private final boolean genericallyTyped = false;

	/**
	 * The type can be a parametrized type
	 */
	private final Map<Method, Class> methodToClass;

	private final Map<Class, Map<String, Type>> classToMap;

	public ClassGenericityResolver(final Class<?> clazz) {
		this.clazz = clazz;
		methodToClass = new HashMap<>();
		classToMap = new HashMap<>();
	}

	/**
	 * Methods of a class could be declare in a parent/grandparent class.
	 * If the genericity is involved, each parent can name the generic object differently.
	 * That's why for each method we need to determine in which class it is declared and resolve
	 * the genericity tree.
	 *
	 * @param method
	 */
	public void initForMethod(final Method method) {
		if(methodToClass.containsKey(method)) {
			return;
		}
		methodToClass.put(method, null);
		final List<ClassType> classTypeList = new ArrayList<>();
		final ClassType first = new ClassType();
		first.clazz = clazz;
		first.type = null;
		classTypeList.add(first);

		Class searched = clazz;
		Type type;
		while(!searched.equals(method.getDeclaringClass())) {
			type = searched.getGenericSuperclass();
			searched = searched.getSuperclass();

			final ClassType classType = new ClassType();
			classType.clazz = searched;
			classType.type = type;
			classTypeList.add(classType);
		}

		methodToClass.put(method, searched);

		if(classToMap.containsKey(searched)) {
			// Genericity has already been resolved for this class, job is done.
			return;
		}

		if(classTypeList.size() > 1) {
			Map<String, Type> fullyResolvedMap = new HashMap<>();
			for(int i = 1; i < classTypeList.size(); i++) {
				if(classTypeList.get(i).type instanceof ParameterizedType) {
					final ParameterizedType pt = (ParameterizedType) classTypeList.get(i).type;
					final Map<String, Type> genericNameToTypeMap = new HashMap<>();
					for(int j = 0; j < pt.getActualTypeArguments().length; j++) {
						if(fullyResolvedMap.containsKey(pt.getActualTypeArguments()[j].getTypeName())) {
							genericNameToTypeMap.put(classTypeList.get(i).clazz.getTypeParameters()[j].getTypeName(),
								fullyResolvedMap.get(pt.getActualTypeArguments()[j].getTypeName()));
						} else {
							genericNameToTypeMap.put(classTypeList.get(i).clazz.getTypeParameters()[j].getTypeName(),
								pt.getActualTypeArguments()[j]);
						}

					}
					fullyResolvedMap = genericNameToTypeMap;
				}
			}
			classToMap.put(searched, fullyResolvedMap);
		}
	}

	private void doContextualSubstitution(final ParameterizedTypeImpl substitution, final Map<String, Type> genericNameToTypeMap,
		final Method method) {
		for(int i = 0; i < substitution.getActualTypeArguments().length; i++) {
			if(genericNameToTypeMap.containsKey(substitution.getActualTypeArguments()[i].getTypeName())) {
				substitution.getActualTypeArguments()[i] =
					genericNameToTypeMap.get(substitution.getActualTypeArguments()[i].getTypeName());
			}
			substitution.getActualTypeArguments()[i] = getContextualType(substitution.getActualTypeArguments()[i], method);
		}
	}

	public Type getContextualType(final Type genericType, final Method method) {
		final Map<String, Type> genericNameToTypeMap = classToMap.get(methodToClass.get(method));
		if(genericNameToTypeMap != null) {
			// It is possible that we will not substitute anything. In that cas, the substitution parameterized type
			// will be equivalent to the source one.
			if(genericType instanceof TypeVariable) {
				final TypeVariable typeVariable = (TypeVariable) genericType;
				if(genericNameToTypeMap.containsKey(typeVariable.getName())) {
					return genericNameToTypeMap.get(typeVariable.getName());
				}
			} else if(genericType instanceof ParameterizedType) {

				final ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(((ParameterizedType) genericType));
				doContextualSubstitution(substitution, genericNameToTypeMap, method);
				return substitution;

			} else if(genericType instanceof GenericArrayType) {
				final GenericArrayType genericArrayType = (GenericArrayType) genericType;
				if(genericArrayType.getGenericComponentType() instanceof ParameterizedType) {
					final ParameterizedTypeImpl substitution = new ParameterizedTypeImpl(
						(ParameterizedType) genericArrayType.getGenericComponentType());
					doContextualSubstitution(substitution, genericNameToTypeMap, method);
					final GenericArrayType substitionArrayType = new GenericArrayTypeImpl(substitution);
					return substitionArrayType;
				} else if(genericArrayType.getGenericComponentType() instanceof TypeVariable<?>) {
					final TypeVariable<?> typeVariable = (TypeVariable<?>) genericArrayType.getGenericComponentType();
					if(genericNameToTypeMap.containsKey(typeVariable.getName())) {
						final GenericArrayType substitionArrayType = new GenericArrayTypeImpl(
							genericNameToTypeMap.get(typeVariable.getName()));
						return substitionArrayType;
					}
				} else {
					throw new RuntimeException("Type : " + ((GenericArrayType) genericType).getGenericComponentType().getClass().toString()
						+ " not handled in generic array contextual substitution.");
				}

			}
		} else if(genericType instanceof ParameterizedType) {
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

			}

		}
		return genericType;
	}

	private class ClassType {

		public Class clazz;
		public Type type;
	}
}
