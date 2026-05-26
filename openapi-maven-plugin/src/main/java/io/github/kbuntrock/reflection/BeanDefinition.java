package io.github.kbuntrock.reflection;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.github.kbuntrock.model.Flow;
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
	private boolean couldDeserialize;
	private boolean couldSerialize;

	private Flow flow;;

	public BeanDefinition(final BeanPropertyDefinition a,
		final BeanPropertyDefinition b, Flow flow) {
		name = a.getName();
		this.flow = flow;
		if(b == null) {
			field = a.getField();
			getter = a.getGetter();
			setter = safelyGetSetter(a);
			internalName = a.getInternalName();
			constructorParameterIsPresent = a.hasConstructorParameter();
			couldSerialize = a.couldSerialize();
			couldDeserialize = a.couldDeserialize();
		} else {
			field = a.getField() != null ? a.getField() : b.getField();
			getter = a.getGetter() != null ? a.getGetter() : b.getGetter();
			setter = safelyGetSetter(a) != null ? safelyGetSetter(a) : safelyGetSetter(b);
			internalName = StringUtils.isNotEmpty(a.getInternalName()) ? a.getInternalName() : b.getInternalName();
			constructorParameterIsPresent = a.hasConstructorParameter() ? a.hasConstructorParameter()
				: b.hasConstructorParameter();
			couldSerialize = a.couldSerialize() || b.couldDeserialize();
			couldDeserialize = a.couldDeserialize() || b.couldDeserialize();
		}
	}

	private AnnotatedMethod safelyGetSetter(BeanPropertyDefinition bpd) {
		try {
			return bpd.getSetter();
		} catch(IllegalArgumentException ex) {
			// Do nothing. No setter can be safely retrieved for this bean (probably a name conflict)
		}
		return null;
	}

	public void merge(final BeanDefinition b, Flow flow) {
		field = field != null ? field : b.getField();
		getter = getter != null ? getter : b.getGetter();
		setter = setter != null ? setter : b.getSetter();
		internalName = StringUtils.isNotEmpty(internalName) ? internalName : b.getInternalName();
		constructorParameterIsPresent = constructorParameterIsPresent || b.hasConstructorParameter();
		couldSerialize = couldSerialize || b.couldDeserialize();
		couldDeserialize = couldDeserialize || b.couldDeserialize();
		if(this.flow != null && this.flow != Flow.INPUT_OUTPUT && this.flow != flow) {
			this.flow = Flow.INPUT_OUTPUT;
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

	public boolean couldDeserialize() {
		return couldDeserialize;
	}

	public boolean couldSerialize() {
		return couldSerialize;
	}

	public boolean compatibleWithFlow(Flow flow) {
		if(Flow.INPUT == flow) {
			return couldDeserialize;
		} else if(Flow.OUTPUT == flow) {
			return couldSerialize;
		}
		return (couldDeserialize | couldSerialize);
	}

	public Flow getFlow() {
		return flow;
	}
}
