package io.github.kbuntrock.reflection.annotation.regular;

import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;
import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A "merged" view follows the Spring Framework semantics: values may come from
 * directly declared annotations, meta-annotations, and from the element's
 * inheritance hierarchy. Nearer declarations override further ones.
 */
public class RegularMergeAnnotations implements MergedAnnotations {

	private final ClassLoaderHelper classLoaderHelper;

	private final AnnotatedElement element;

	private final AnnotatedElementType annotatedElementType;

	/**
	 * Create a merged annotation view for the given annotated element.
	 * Determines the element kind (class, method, field, or parameter) up front.
	 *
	 * @param classLoaderHelper
	 *            helper used to resolve annotation types by name
	 * @param element
	 *            the annotated element to inspect
	 */
	public RegularMergeAnnotations(ClassLoaderHelper classLoaderHelper, AnnotatedElement element) {
		this.classLoaderHelper = classLoaderHelper;
		this.element = element;

		if(Method.class.isAssignableFrom(element.getClass())) {
			annotatedElementType = AnnotatedElementType.METHOD;
		} else if(Class.class.isAssignableFrom(element.getClass())) {
			annotatedElementType = AnnotatedElementType.CLASS;
		} else if(Field.class.isAssignableFrom(element.getClass())) {
			annotatedElementType = AnnotatedElementType.FIELD;
		} else if(Parameter.class.isAssignableFrom(element.getClass())) {
			annotatedElementType = AnnotatedElementType.PARAMETER;
		} else {
			annotatedElementType = AnnotatedElementType.NOT_SUPPORTED;
		}
	}

	/**
	 * Return a merged view of the annotation identified by its fully qualified name
	 * on the underlying element. The search includes direct annotations, meta-annotations,
	 * and the element's inheritance hierarchy. Nearer declarations override further ones.
	 *
	 * @param annotationType
	 *            fully qualified annotation class name
	 * @return a merged annotation; if the annotation is not present, an empty view is returned
	 */
	@Override
	public MergedAnnotation get(String annotationType) {
		if(AnnotatedElementType.NOT_SUPPORTED != annotatedElementType) {
			Optional<Class> clazz = classLoaderHelper.tryToGetByName(annotationType);
			// Return promptly if the class we are looking for is not in the classpath nor an annotation.
			if(!clazz.isPresent() || !Annotation.class.isAssignableFrom(clazz.get())) {
				return new RegularMergedAnnotation(false);
			}
			Class<? extends Annotation> annType = (Class<? extends Annotation>) clazz.get();

			if(AnnotatedElementType.METHOD == annotatedElementType) {
				return findMergedOnMethod((Method) element, annType);
			} else if(AnnotatedElementType.CLASS == annotatedElementType) {
				return findMergedOnClass((Class) element, annType);
			} else if(AnnotatedElementType.FIELD == annotatedElementType) {
				return findMergedOnField((Field) element, annType);
			} else if(AnnotatedElementType.PARAMETER == annotatedElementType) {
				return findMergedOnParameter((Parameter) element, annType);
			}
		}
		return new RegularMergedAnnotation(false);
	}

	private RegularMergedAnnotation findMergedOnClass(Class<?> target, Class<? extends Annotation> annotationType) {
		List<Candidate> candidates = new ArrayList<>();
		collectOnClassHierarchy(target, annotationType, candidates, 0, new HashSet<>());
		return merge(annotationType, candidates);
	}

	private RegularMergedAnnotation findMergedOnMethod(Method target, Class<? extends Annotation> annotationType) {
		List<Candidate> candidates = new ArrayList<>();
		collectOnMethodHierarchy(target, annotationType, candidates, 0, new HashSet<>());
		return merge(annotationType, candidates);
	}

	private RegularMergedAnnotation findMergedOnField(Field target, Class<? extends Annotation> annotationType) {
		List<Candidate> candidates = new ArrayList<>();
		collectOnFieldHierarchy(target, annotationType, candidates, 0, new HashSet<>());
		return merge(annotationType, candidates);
	}

	private RegularMergedAnnotation findMergedOnParameter(Parameter target, Class<? extends Annotation> annotationType) {
		List<Candidate> candidates = new ArrayList<>();
		collectOnParameterHierarchy(target, annotationType, candidates, 0, new HashSet<>());
		return merge(annotationType, candidates);
	}

	// -------- Merge logic --------

	private static <A extends Annotation> RegularMergedAnnotation merge(Class<A> annType, List<Candidate> candidates) {

		RegularMergedAnnotation regularMergedAnnotation = new RegularMergedAnnotation(!candidates.isEmpty());
		// Return promptly of not candidate has been found.
		if(!regularMergedAnnotation.isPresent()) {
			return regularMergedAnnotation;
		}

		Map<String, Object> merged = new HashMap<>();
		regularMergedAnnotation.setMergedAttributes(merged);
		regularMergedAnnotation.setTypeName(annType.getCanonicalName());

		// Far → near (near overrides)
		List<Candidate> farFirst = candidates.stream()
			.sorted(Comparator.comparingInt((Candidate c) -> c.distance).reversed())
			.collect(Collectors.toList());

		for(Candidate c : farFirst) {
			Map<String, Object> values = RegularMergedAnnotation.readAttributesForTarget(c.annotation, annType);
			if(values != null) {
				merged.putAll(values);
			}
		}

		return regularMergedAnnotation;
	}

	// -------- Collection by TYPE_HIERARCHY (proximity) --------

	private static <A extends Annotation> void collectOnClassHierarchy(
		Class<?> cls, Class<A> annType, List<Candidate> out, int distance, Set<Class<?>> visitedTypes) {

		if(cls == null || !visitedTypes.add(cls)) {
			return;
		}

		collectOnElement(cls, annType, out, distance);

		collectOnClassHierarchy(cls.getSuperclass(), annType, out, distance + 1, visitedTypes);

		for(Class<?> itf : cls.getInterfaces()) {
			collectOnClassHierarchy(itf, annType, out, distance + 1, visitedTypes);
		}
	}

	private static <A extends Annotation> void collectOnFieldHierarchy(
		Field field, Class<A> annType, List<Candidate> out, int distance,
		Set<Class<?>> visitedTypes) {

		collectOnElement(field, annType, out, distance);

		Class<?> decl = field.getDeclaringClass();

		Class<?> superCls = decl.getSuperclass();
		if(superCls != null) {
			Field f = findField(superCls, field.getName());
			if(f != null) {
				collectOnFieldHierarchy(f, annType, out, distance + 1, visitedTypes);
			} else {
				collectOnFieldHierarchyUp(field, superCls, annType, out, distance + 1, visitedTypes);
			}
		}

		for(Class<?> itf : decl.getInterfaces()) {
			Field f = findField(superCls, field.getName());
			if(f != null) {
				collectOnFieldHierarchy(f, annType, out, distance + 1, visitedTypes);
			} else {
				collectOnFieldHierarchyUp(field, itf, annType, out, distance + 1, visitedTypes);
			}
		}
	}

	private static <A extends Annotation> void collectOnFieldHierarchyUp(
		Field baseField, Class<?> type, Class<A> annType, List<Candidate> out, int distance,
		Set<Class<?>> visitedTypes) {

		if(type == null || !visitedTypes.add(type)) {
			return;
		}

		Field f = findField(type, baseField.getName());
		if(f != null) {
			collectOnFieldHierarchy(f, annType, out, distance, visitedTypes);
		}

		collectOnFieldHierarchyUp(baseField, type.getSuperclass(), annType, out, distance + 1, visitedTypes);
		for(Class<?> itf : type.getInterfaces()) {
			collectOnFieldHierarchyUp(baseField, itf, annType, out, distance + 1, visitedTypes);
		}
	}

	private static <A extends Annotation> void collectOnMethodHierarchy(
		Method method, Class<A> annType, List<Candidate> out, int distance,
		Set<Class<?>> visitedTypes) {

		if(method.isBridge()) {
			// There is no need to scan bridge methods
			return;
		}

		collectOnElement(method, annType, out, distance);

		Class<?> decl = method.getDeclaringClass();

		Class<?> superCls = decl.getSuperclass();
		if(superCls != null) {
			Method m = findMethod(superCls, method.getName(), method.getParameterTypes());
			if(m != null) {
				collectOnMethodHierarchy(m, annType, out, distance + 1, visitedTypes);
			} else {
				collectOnMethodHierarchyUp(method, superCls, annType, out, distance + 1, visitedTypes);
			}
		}

		for(Class<?> itf : decl.getInterfaces()) {
			Method m = findMethod(itf, method.getName(), method.getParameterTypes());
			if(m != null) {
				collectOnMethodHierarchy(m, annType, out, distance + 1, visitedTypes);
			} else {
				collectOnMethodHierarchyUp(method, itf, annType, out, distance + 1, visitedTypes);
			}
		}
	}

	private static <A extends Annotation> void collectOnMethodHierarchyUp(
		Method baseMethod, Class<?> type, Class<A> annType, List<Candidate> out, int distance,
		Set<Class<?>> visitedTypes) {

		if(type == null || !visitedTypes.add(type)) {
			return;
		}

		Method m = findMethod(type, baseMethod.getName(), baseMethod.getParameterTypes());
		if(m != null) {
			collectOnMethodHierarchy(m, annType, out, distance, visitedTypes);
		}

		collectOnMethodHierarchyUp(baseMethod, type.getSuperclass(), annType, out, distance + 1, visitedTypes);
		for(Class<?> itf : type.getInterfaces()) {
			collectOnMethodHierarchyUp(baseMethod, itf, annType, out, distance + 1, visitedTypes);
		}
	}

	private static <A extends Annotation> void collectOnParameterHierarchy(
		Parameter parameter, Class<A> annType, List<Candidate> out, int distance,
		Set<Class<?>> visitedTypes) {

		// Annotations directly on the current parameter
		collectOnElement(parameter, annType, out, distance);

		Executable exec = parameter.getDeclaringExecutable();
		if(!(exec instanceof Method)) {
			return; // no hierarchy traversal for constructors here
		}
		Method method = (Method) exec;

		// find the parameter index within the method
		int index = -1;
		Parameter[] params = method.getParameters();
		for(int i = 0; i < params.length; i++) {
			if(params[i].equals(parameter)) { // equality tied to the (executable, index) pair
				index = i;
				break;
			}
		}
		if(index < 0) {
			return;
		}

		Class<?> decl = method.getDeclaringClass();

		// super class
		Class<?> superCls = decl.getSuperclass();
		if(superCls != null) {
			Method m = findMethod(superCls, method.getName(), method.getParameterTypes());
			if(m != null) {
				collectOnParameterHierarchy(m.getParameters()[index], annType, out, distance + 1, visitedTypes);
			} else {
				collectOnParameterHierarchyUp(method, index, superCls, annType, out, distance + 1, visitedTypes);
			}
		}

		// interfaces
		for(Class<?> itf : decl.getInterfaces()) {
			Method m = findMethod(itf, method.getName(), method.getParameterTypes());
			if(m != null) {
				collectOnParameterHierarchy(m.getParameters()[index], annType, out, distance + 1, visitedTypes);
			} else {
				collectOnParameterHierarchyUp(method, index, itf, annType, out, distance + 1, visitedTypes);
			}
		}
	}

	private static <A extends Annotation> void collectOnParameterHierarchyUp(
		Method baseMethod, int paramIndex, Class<?> type, Class<A> annType,
		List<Candidate> out, int distance, Set<Class<?>> visitedTypes) {

		if(type == null || !visitedTypes.add(type)) {
			return;
		}

		Method m = findMethod(type, baseMethod.getName(), baseMethod.getParameterTypes());
		if(m != null) {
			collectOnParameterHierarchy(m.getParameters()[paramIndex], annType, out, distance, visitedTypes);
		}

		collectOnParameterHierarchyUp(baseMethod, paramIndex, type.getSuperclass(), annType, out, distance + 1, visitedTypes);
		for(Class<?> itf : type.getInterfaces()) {
			collectOnParameterHierarchyUp(baseMethod, paramIndex, itf, annType, out, distance + 1, visitedTypes);
		}
	}

	private static Method findMethod(Class<?> type, String name, Class<?>[] paramTypes) {
		if(type == null) {
			return null;
		}
		try {
			return type.getDeclaredMethod(name, paramTypes);
		} catch(NoSuchMethodException e) {
			return null;
		}
	}

	private static Field findField(Class<?> type, String name) {
		if(type == null) {
			return null;
		}
		try {
			return type.getDeclaredField(name);
		} catch(NoSuchFieldException e) {
			return null;
		}
	}

	private static <A extends Annotation> void collectOnElement(
		AnnotatedElement element, Class<A> annType, List<Candidate> out, int distance) {

		// Direct
		A direct = element.getAnnotation(annType);
		if(direct != null) {
			out.add(new Candidate(direct, distance));
		}

		// Directly present annotations
		for(Annotation ann : element.getAnnotations()) {
			Class<? extends Annotation> sourceType = ann.annotationType();

			// Meta-annotations (distance + 1)
			A metaOnSource = sourceType.getAnnotation(annType);
			if(metaOnSource != null) {
				out.add(new Candidate(metaOnSource, distance + 1));
			}

			// Explore meta-of-meta to find other occurrences of the target
			collectFromMeta(sourceType, annType, out, distance + 1, new HashSet<>());
		}
	}

	private static <A extends Annotation> void collectFromMeta(
		Class<? extends Annotation> metaSource, Class<A> annType,
		List<Candidate> out, int distance, Set<Class<?>> visited) {

		if(!visited.add(metaSource)) {
			return;
		}

		for(Annotation higher : metaSource.getAnnotations()) {
			Class<? extends Annotation> higherType = higher.annotationType();
			if(!higherType.getName().startsWith("java.lang")) {
				A target = higherType.getAnnotation(annType);
				if(target != null) {
					out.add(new Candidate(target, distance + 1));
				}
				collectFromMeta(higherType, annType, out, distance + 1, visited);
			}
		}
	}

	// -------- Helpers --------

	private static Map<String, Object> defaultsOf(Class<? extends Annotation> annType) {
		Map<String, Object> map = new LinkedHashMap<>();
		for(Method m : annType.getDeclaredMethods()) {
			Object def = m.getDefaultValue();
			if(def != null) {
				map.put(m.getName(), def);
			}
		}
		return map;
	}

	private static final class Candidate {
		final Annotation annotation;
		final int distance;

		Candidate(Annotation annotation, int distance) {
			this.annotation = annotation;
			this.distance = distance;
		}
	}

	private enum AnnotatedElementType {
		METHOD,
		CLASS,
		FIELD,
		PARAMETER,
		NOT_SUPPORTED;
	}
}
