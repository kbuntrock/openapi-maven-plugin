package io.github.kbuntrock.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Kevin Buntrock
 */
public class ParameterizedTypeImpl implements ParameterizedType {

	private Type[] actualTypeArguments;
	private Type rawType;
	private Type ownerType;

	public ParameterizedTypeImpl() {
	}

	public ParameterizedTypeImpl(ParameterizedType parameterizedType) {
		this.actualTypeArguments = parameterizedType.getActualTypeArguments();
		this.rawType = parameterizedType.getRawType();
		this.ownerType = parameterizedType.getOwnerType();
	}

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments;
	}

	public void setActualTypeArguments(Type[] actualTypeArguments) {
		this.actualTypeArguments = actualTypeArguments;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	public void setRawType(Type rawType) {
		this.rawType = rawType;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}
}
