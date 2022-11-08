package io.github.kbuntrock.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * @author Kevin Buntrock
 */
public class GenericArrayTypeImpl implements GenericArrayType {

	private final Type genericComponentType;

	public GenericArrayTypeImpl(final Type genericComponentType) {
		this.genericComponentType = genericComponentType;
	}

	@Override
	public Type getGenericComponentType() {
		return genericComponentType;
	}
}
