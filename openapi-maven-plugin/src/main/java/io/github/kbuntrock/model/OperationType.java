package io.github.kbuntrock.model;

import java.util.HashMap;
import java.util.Map;

public enum OperationType {
	GET("GET", "javax.ws.rs.GET", "jakarta.ws.rs.GET"),
	POST("POST", "javax.ws.rs.POST", "jakarta.ws.rs.POST"),
	PUT("PUT", "javax.ws.rs.PUT", "jakarta.ws.rs.PUT"),
	PATCH("PATCH", "javax.ws.rs.PATCH", "jakarta.ws.rs.PATCH"),
	DELETE("DELETE", "javax.ws.rs.DELETE", "jakarta.ws.rs.DELETE"),
	HEAD("HEAD", "javax.ws.rs.HEAD", "jakarta.ws.rs.HEAD"),
	OPTIONS("OPTIONS", "javax.ws.rs.OPTIONS", "jakarta.ws.rs.OPTIONS"),
	TRACE("TRACE", null, null);

	private static final Map<String, OperationType> mapBySpringMvcRequestMethod = new HashMap<>();
	private static final Map<String, OperationType> mapByJavaxRsAnnotationClass = new HashMap<>();
	private static final Map<String, OperationType> mapByJakartaRsAnnotationClass = new HashMap<>();

	static {
		for(final OperationType type : OperationType.values()) {
			mapBySpringMvcRequestMethod.put(type.springMvcRequestMethod, type);
			mapByJavaxRsAnnotationClass.put(type.javaxRsVerbAnnotation, type);
			mapByJakartaRsAnnotationClass.put(type.jakartaRsVerbAnnotation, type);
		}
	}

	private final String springMvcRequestMethod;
	private final String javaxRsVerbAnnotation;
	private final String jakartaRsVerbAnnotation;

	OperationType(final String springMvcRequestMethod, final String javaxRsVerbAnnotation,
		final String jakartaRsVerbAnnotation) {
		this.springMvcRequestMethod = springMvcRequestMethod;
		this.javaxRsVerbAnnotation = javaxRsVerbAnnotation;
		this.jakartaRsVerbAnnotation = jakartaRsVerbAnnotation;
	}

	public static OperationType fromSpring(final String springMvcRequestMethod) {
		return mapBySpringMvcRequestMethod.get(springMvcRequestMethod);
	}

	public static OperationType fromJavax(final String javaxRsVerbAnnotation) {
		return mapByJavaxRsAnnotationClass.get(javaxRsVerbAnnotation);
	}

	public static OperationType fromJakarta(final String jakartaRsVerbAnnotation) {
		return mapByJakartaRsAnnotationClass.get(jakartaRsVerbAnnotation);
	}

}
