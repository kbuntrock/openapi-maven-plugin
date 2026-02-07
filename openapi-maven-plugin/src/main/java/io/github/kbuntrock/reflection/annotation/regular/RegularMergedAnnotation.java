package io.github.kbuntrock.reflection.annotation.regular;

import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The merged view follows the Spring Framework concept of "merged annotations":
 * attributes may be sourced from annotations declared directly on the element,
 * from meta-annotations, and along the relevant inheritance hierarchy of the
 * underlying element (type, method, field, or parameter). When multiple sources
 * provide a value for the same attribute, nearer declarations override farther ones.
 */
public class RegularMergedAnnotation implements MergedAnnotation {

	public static final String CANNOT_BE_CALLED_ON_NOT_PRESENT = "Cannot retrieve annotation attribute if the annotation is not present. "
		+ "(Please create a github issue)";

	private String typeName;

	private boolean present;

	private Map<String, Object> mergedAttributes;

	RegularMergedAnnotation(boolean present) {
		this.present = present;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setMergedAttributes(Map<String, Object> mergedAttributes) {
		this.mergedAttributes = mergedAttributes;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	/**
	 * Whether the target annotation is present after merging.
	 *
	 * @return true if the annotation is present, false otherwise
	 */
	@Override
	public boolean isPresent() {
		return present;
	}

	/**
	 * Get a boolean attribute value from the merged annotation.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the boolean value
	 */
	@Override
	public boolean getBoolean(String attributeName) {
		checkIfPresent();
		return getAttributeValue(attributeName, Boolean.class);
	}

	/**
	 * Get an int attribute value from the merged annotation.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the int value
	 */
	@Override
	public int getInt(String attributeName) {
		checkIfPresent();
		return getAttributeValue(attributeName, Integer.class);
	}

	/**
	 * Get an enum array attribute as an array of enum constant names.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return array of enum names
	 */
	@Override
	public String[] getEnumArrayAsString(String attributeName) {
		checkIfPresent();
		Class<?> arrayType = Array.newInstance(Enum.class, 0).getClass();
		Enum[] array = (Enum[]) getAttributeValue(attributeName, arrayType);
		return Arrays.stream(array).map(Enum::name).toArray(String[]::new);
	}

	/**
	 * Get an annotation array attribute, converting each element into a merged annotation.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return array of merged annotations
	 */
	@Override
	public MergedAnnotation[] getAnnotationArray(String attributeName) {
		checkIfPresent();
		Annotation[] annotation = getAttributeValue(attributeName, Annotation[].class);
		List<MergedAnnotation> mergedAnnotations = new ArrayList<>();
		for(Annotation a : annotation) {
			mergedAnnotations.add(computeMergedAnnotationFromAnnotation(a));
		}
		return mergedAnnotations.toArray(new MergedAnnotation[0]);
	}

	/**
	 * Get a nested annotation attribute as a merged annotation.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the nested annotation as a merged view
	 */
	@Override
	public MergedAnnotation getAnnotation(String attributeName) {
		checkIfPresent();
		Annotation annotation = getAttributeValue(attributeName, Annotation.class);
		return computeMergedAnnotationFromAnnotation(annotation);
	}

	private static RegularMergedAnnotation computeMergedAnnotationFromAnnotation(Annotation annotation) {
		Map<String, Object> values = readAttributesForTarget(annotation, annotation.annotationType());
		if(values != null) {
			RegularMergedAnnotation mergedAnnotation = new RegularMergedAnnotation(true);
			mergedAnnotation.mergedAttributes = values;
			return mergedAnnotation;
		}
		return new RegularMergedAnnotation(false);
	}

	/**
	 * Get a raw attribute value.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return an Optional containing the value, or empty if the attribute is absent
	 */
	@Override
	public Optional<Object> getValue(String attributeName) {
		checkIfPresent();
		Object result = mergedAttributes.get(attributeName);
		if(result == null) {
			return Optional.empty();
		}
		return Optional.of(result);
	}

	/**
	 * Get a String attribute value.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the String value
	 */
	@Override
	public String getString(String attributeName) {
		checkIfPresent();
		return getAttributeValue(attributeName, String.class);
	}

	/**
	 * Get a String array attribute value.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the String array value
	 */
	@Override
	public String[] getStringArray(String attributeName) {
		checkIfPresent();
		return getAttributeValue(attributeName, String[].class);
	}

	/**
	 * Get a Class attribute value.
	 *
	 * @param attributeName
	 *            the attribute name
	 * @return the Class value
	 */
	@Override
	public Class<?> getClass(String attributeName) {
		checkIfPresent();
		return getAttributeValue(attributeName, Class.class);
	}

	private <T> T getAttributeValue(String attributeName, Class<T> type) {
		Object value = mergedAttributes.get(attributeName);
		if(value == null) {
			throw new NoSuchElementException("No attribute '" + attributeName + "' in merged annotation " + typeName);
		}
		if(type.isAssignableFrom(value.getClass())) {
			return type.cast(value);
		}
		throw new IllegalArgumentException("Attribute '" + attributeName + "' of type " + value.getClass().getTypeName()
			+ " cannot be cast into " + type.getTypeName() + "(merged annotation " + typeName + ")");
	}

	private void checkIfPresent() {
		if(!present) {
			throw new MojoRuntimeException(CANNOT_BE_CALLED_ON_NOT_PRESENT);
		}
	}

	static Map<String, Object> readAttributesForTarget(Annotation ann, Class<? extends Annotation> targetAnnType) {
		Class<? extends Annotation> sourceType = ann.annotationType();
		if(sourceType == targetAnnType) {
			Map<String, Object> values = new HashMap<>();
			for(Method m : targetAnnType.getDeclaredMethods()) {
				if(m.getParameterCount() == 0) {
					Object v = invokeQuiet(ann, m);
					values.put(m.getName(), v);
				}
			}
			return values;
		}
		return null;
	}

	private static Object invokeQuiet(Annotation ann, Method m) {
		try {
			return m.invoke(ann);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
