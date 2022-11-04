package io.github.kbuntrock.model;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMethod;

public enum OperationType {
	GET(RequestMethod.GET, javax.ws.rs.GET.class, "jakarta.ws.rs.GET"),
	POST(RequestMethod.POST, javax.ws.rs.POST.class, "jakarta.ws.rs.POST"),
	PUT(RequestMethod.PUT, javax.ws.rs.PUT.class, "jakarta.ws.rs.PUT"),
	PATCH(RequestMethod.PATCH, javax.ws.rs.PATCH.class, "jakarta.ws.rs.PATCH"),
	DELETE(RequestMethod.DELETE, javax.ws.rs.DELETE.class, "jakarta.ws.rs.DELETE"),
	HEAD(RequestMethod.HEAD, javax.ws.rs.HEAD.class, "jakarta.ws.rs.HEAD"),
	OPTIONS(RequestMethod.OPTIONS, javax.ws.rs.OPTIONS.class, "jakarta.ws.rs.OPTIONS"),
	TRACE(RequestMethod.TRACE, null, null);

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

	OperationType(final RequestMethod springMvcRequestMethod, final Class javaxRsVerbAnnotation, final String jakartaRsVerbAnnotation) {
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
}
