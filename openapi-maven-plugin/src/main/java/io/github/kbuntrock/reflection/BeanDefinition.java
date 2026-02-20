package io.github.kbuntrock.reflection;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * Classes that contain definitions of properties used for serialisation and deserialization.
 * Based on jackson BeanPropertyDefinition. The need of a custom one comes from the fact that we want to mix serialisation and
 * deserialization information.
 *
 */
public class BeanDefinition {

	private AnnotatedField field;
	private AnnotatedMethod getter;
	private AnnotatedMethod setter;
	private String internalName;
	private boolean constructorParameterIsPresent;

	public BeanDefinition(final BeanPropertyDefinition deserializationDefinition,
		final BeanPropertyDefinition serializationDefinition) {
	}

	public boolean hasConstructorParameter() {
		return constructorParameterIsPresent;
	}

	public boolean hasField() {
		return field != null;
	}

	public AnnotatedField getField() {
		return field;
	}

	public boolean hasGetter() {
		return getter != null;
	}

	public AnnotatedMethod getGetter() {
		return getter;
	}

	public boolean hasSetter() {
		return setter != null;
	}

	public AnnotatedMethod getSetter() {
		return setter;
	}

	public String getInternalName() {
		return internalName;
	}
}
