package io.github.kbuntrock.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decides whether a property is required (non nullable) or not, based on the configured nullable / non nullable
 * annotations, the JSpecify {@code @NullMarked} / {@code @NullUnmarked} scopes and the global
 * {@code defaultNonNullableFields} setting.
 */
public class NullableConfiguration {

	private static final String defaultJakartaNullable = "jakarta.annotation.Nullable";
	private static final String defaultJavaxNullable = "javax.annotation.Nullable";
	private static final String defaultJSpecifyNullable = "org.jspecify.annotations.Nullable";

	private static final String defaultJakartaNotNull = "jakarta.validation.constraints.NotNull";
	private static final String defaultJakartaNotBlank = "jakarta.validation.constraints.NotBlank";
	private static final String defaultJakartaNotEmpty = "jakarta.validation.constraints.NotEmpty";
	private static final String defaultJavaxNotNull = "javax.validation.constraints.NotNull";
	private static final String defaultJavaxNotBlank = "javax.validation.constraints.NotBlank";
	private static final String defaultJavaxNotEmpty = "javax.validation.constraints.NotEmpty";
	private static final String defaultJSpecifyNonNull = "org.jspecify.annotations.NonNull";

	private static final String NULL_MARKED = "org.jspecify.annotations.NullMarked";
	private static final String NULL_UNMARKED = "org.jspecify.annotations.NullUnmarked";

	private final List<String> nullableAnnotations;
	private final List<String> nonNullAnnotations;
	private final boolean defaultNonNullableFields;

	/**
	 * Nullness scope resolved for a class (enclosing classes, package and module included). An empty optional means
	 * that no JSpecify scope annotation applies.
	 */
	private final Map<Class<?>, Optional<Boolean>> classScopeCache = new ConcurrentHashMap<>();

	public NullableConfiguration(final CommonApiConfiguration commonApiConfiguration) {
		defaultNonNullableFields = commonApiConfiguration.defaultNonNullableFields != null
			&& commonApiConfiguration.defaultNonNullableFields;

		nullableAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nullableAnnotation != null) {
			nullableAnnotations.addAll(commonApiConfiguration.nullableAnnotation);
		} else {
			nullableAnnotations.add(defaultJakartaNullable);
			nullableAnnotations.add(defaultJavaxNullable);
			nullableAnnotations.add(defaultJSpecifyNullable);
		}
		nonNullAnnotations = new ArrayList<>();
		if(commonApiConfiguration.nonNullableAnnotation != null) {
			nonNullAnnotations.addAll(commonApiConfiguration.nonNullableAnnotation);
		} else {
			nonNullAnnotations.add(defaultJakartaNotNull);
			nonNullAnnotations.add(defaultJakartaNotBlank);
			nonNullAnnotations.add(defaultJakartaNotEmpty);
			nonNullAnnotations.add(defaultJavaxNotNull);
			nonNullAnnotations.add(defaultJavaxNotBlank);
			nonNullAnnotations.add(defaultJavaxNotEmpty);
			nonNullAnnotations.add(defaultJSpecifyNonNull);
		}

	}

	private List<String> getNullableAnnotations() {
		return nullableAnnotations;
	}

	private List<String> getNonNullAnnotations() {
		return nonNullAnnotations;
	}

	/**
	 * @return the global default, which does not take JSpecify scopes into account. Prefer
	 *         {@link #isDefaultNonNullable(Member)} when the java member is known.
	 */
	public boolean isDefaultNonNullableFields() {
		return defaultNonNullableFields;
	}

	/**
	 * Resolves whether an unannotated member is required by default. The closest JSpecify scope annotation wins
	 * (method, declaring class, enclosing methods and classes, package, then module): {@code @NullMarked} makes it
	 * required, {@code @NullUnmarked} makes it not required. Without any scope annotation, the global
	 * {@code defaultNonNullableFields} setting applies.
	 *
	 * @param member
	 *            the field or accessor method backing the property. Can be null.
	 * @return true if the member should be considered as required by default
	 */
	public boolean isDefaultNonNullable(final Member member) {
		if(member == null) {
			return defaultNonNullableFields;
		}
		if(member instanceof AnnotatedElement) {
			final Boolean memberScope = readScope((AnnotatedElement) member);
			if(memberScope != null) {
				return memberScope;
			}
		}
		return resolveClassScope(member.getDeclaringClass()).orElse(defaultNonNullableFields);
	}

	private Optional<Boolean> resolveClassScope(final Class<?> clazz) {
		final Optional<Boolean> cached = classScopeCache.get(clazz);
		if(cached != null) {
			return cached;
		}
		final Optional<Boolean> resolved = Optional.ofNullable(computeClassScope(clazz));
		classScopeCache.put(clazz, resolved);
		return resolved;
	}

	private Boolean computeClassScope(final Class<?> clazz) {
		Boolean scope = readScope(clazz);
		if(scope != null) {
			return scope;
		}
		// Local and anonymous classes are scoped by the method or constructor declaring them
		if(clazz.getEnclosingMethod() != null) {
			scope = readScope(clazz.getEnclosingMethod());
		} else if(clazz.getEnclosingConstructor() != null) {
			scope = readScope(clazz.getEnclosingConstructor());
		}
		if(scope != null) {
			return scope;
		}
		final Class<?> enclosingClass = clazz.getEnclosingClass();
		if(enclosingClass != null) {
			return resolveClassScope(enclosingClass).orElse(null);
		}
		final Package pkg = clazz.getPackage();
		if(pkg != null) {
			scope = readScope(pkg);
			if(scope != null) {
				return scope;
			}
		}
		return readModuleScope(clazz);
	}

	/**
	 * Modules only exist since Java 9 while the plugin is compiled for Java 8, hence the reflective access.
	 */
	private Boolean readModuleScope(final Class<?> clazz) {
		try {
			final Method getModule = Class.class.getMethod("getModule");
			final Object module = getModule.invoke(clazz);
			if(module instanceof AnnotatedElement) {
				return readScope((AnnotatedElement) module);
			}
		} catch(final ReflectiveOperationException | RuntimeException e) {
			// Running on Java 8 or module not accessible: no module scope
		}
		return null;
	}

	private static Boolean readScope(final AnnotatedElement element) {
		boolean nullMarked = false;
		boolean nullUnmarked = false;
		for(final Annotation annotation : element.getAnnotations()) {
			final String name = annotation.annotationType().getName();
			if(NULL_MARKED.equals(name)) {
				nullMarked = true;
			} else if(NULL_UNMARKED.equals(name)) {
				nullUnmarked = true;
			}
		}
		// Conflicting annotations carry no usable intent: the outer scope decides
		if(nullMarked == nullUnmarked) {
			return null;
		}
		return nullMarked;
	}

	public boolean hasNullableAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNullableAnnotations().contains(name));
	}

	public boolean hasNonNullAnnotation(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getNonNullAnnotations().contains(name));
	}
}
