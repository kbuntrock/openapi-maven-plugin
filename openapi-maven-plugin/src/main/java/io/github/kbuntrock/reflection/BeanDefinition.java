package io.github.kbuntrock.reflection;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * Classes that contain definitions of properties used for serialisation and deserialization.
 * Based on jackson BeanPropertyDefinition. The need of a custom one comes from the fact that we want to mix serialisation and
 * deserialization information.
 *
 */
public class BeanDefinition {

	private String name;
	private AnnotatedField field;
	private AnnotatedMethod getter;
	private AnnotatedMethod setter;
	private String internalName;
	private boolean constructorParameterIsPresent;

	public BeanDefinition(final BeanPropertyDefinition a,
		final BeanPropertyDefinition b) {
		name = a.getName();
		if(b == null) {
			field = a.getField();
			getter = a.getGetter();
			setter = a.getSetter();
			internalName = a.getInternalName();
			constructorParameterIsPresent = a.hasConstructorParameter();
		} else {
			field = a.getField() != null ? a.getField() : b.getField();
			getter = a.getGetter() != null ? a.getGetter() : b.getGetter();
			setter = a.getSetter() != null ? a.getSetter() : b.getSetter();
			internalName = StringUtils.isNotEmpty(a.getInternalName()) ? a.getInternalName() : b.getInternalName();
			constructorParameterIsPresent = a.hasConstructorParameter() ? a.hasConstructorParameter()
				: b.hasConstructorParameter();
		}
	}

	public String getName() {
		return name;
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
