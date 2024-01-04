package io.github.kbuntrock.model;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMethod;

public enum OperationType {
	GET(1, RequestMethod.GET, javax.ws.rs.GET.class, "jakarta.ws.rs.GET"),
	POST(2, RequestMethod.POST, javax.ws.rs.POST.class, "jakarta.ws.rs.POST"),
	PUT(3, RequestMethod.PUT, javax.ws.rs.PUT.class, "jakarta.ws.rs.PUT"),
	PATCH(4, RequestMethod.PATCH, javax.ws.rs.PATCH.class, "jakarta.ws.rs.PATCH"),
	DELETE(5, RequestMethod.DELETE, javax.ws.rs.DELETE.class, "jakarta.ws.rs.DELETE"),
	HEAD(6, RequestMethod.HEAD, javax.ws.rs.HEAD.class, "jakarta.ws.rs.HEAD"),
	OPTIONS(7, RequestMethod.OPTIONS, javax.ws.rs.OPTIONS.class, "jakarta.ws.rs.OPTIONS"),
	TRACE(8, RequestMethod.TRACE, null, null);

	private static final Map<RequestMethod, OperationType> mapBySpringMvcRequestMethod = new HashMap<>();
	private static final Map<Class, OperationType> mapByJavaxRsAnnotationClass = new HashMap<>();
	private static final Map<String, OperationType> mapByJakartaRsAnnotationClass = new HashMap<>();

	static {
		for(final OperationType type : OperationType.values()) {
			mapBySpringMvcRequestMethod.put(type.springMvcRequestMethod, type);
			mapByJavaxRsAnnotationClass.put(type.javaxRsVerbAnnotation, type);
			mapByJakartaRsAnnotationClass.put(type.jakartaRsVerbAnnotation, type);
		}
	}

	private final RequestMethod springMvcRequestMethod;
	private final Class javaxRsVerbAnnotation;
	private final String jakartaRsVerbAnnotation;

	/**
	 * Restitution order in the generated documentation for same urls
	 */
	private final int schemaOrder;

	OperationType(final int schemaOrder, final RequestMethod springMvcRequestMethod, final Class javaxRsVerbAnnotation,
		final String jakartaRsVerbAnnotation) {
		this.schemaOrder = schemaOrder;
		this.springMvcRequestMethod = springMvcRequestMethod;
		this.javaxRsVerbAnnotation = javaxRsVerbAnnotation;
		this.jakartaRsVerbAnnotation = jakartaRsVerbAnnotation;
	}

	public static OperationType fromJavax(final RequestMethod springMvcRequestMethod) {
		return mapBySpringMvcRequestMethod.get(springMvcRequestMethod);
	}

	public static OperationType fromJavax(final Class javaxRsVerbAnnotation) {
		return mapByJavaxRsAnnotationClass.get(javaxRsVerbAnnotation);
	}

	public static OperationType fromJakarta(final String jakartaRsVerbAnnotation) {
		return mapByJakartaRsAnnotationClass.get(jakartaRsVerbAnnotation);
	}

	public int getSchemaOrder() {
		return schemaOrder;
	}
}
