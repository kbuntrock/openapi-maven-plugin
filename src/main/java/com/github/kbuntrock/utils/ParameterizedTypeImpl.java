package com.github.kbuntrock.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Kevin Buntrock
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    Type[] actualTypeArguments;
    Type rawType;
    Type ownerType;

    public ParameterizedTypeImpl(ParameterizedType parameterizedType) {
        this.actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.rawType = parameterizedType.getRawType();
        this.ownerType = parameterizedType.getOwnerType();
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }
}
