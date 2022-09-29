package io.github.kbuntrock.model;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.GET;
import java.util.HashMap;
import java.util.Map;

public enum OperationType {
    GET(RequestMethod.GET, javax.ws.rs.GET.class),
    POST(RequestMethod.POST, javax.ws.rs.POST.class),
    PUT(RequestMethod.PUT, javax.ws.rs.PUT.class),
    PATCH(RequestMethod.PATCH, javax.ws.rs.PATCH.class),
    DELETE(RequestMethod.DELETE, javax.ws.rs.DELETE.class),
    HEAD(RequestMethod.HEAD, javax.ws.rs.HEAD.class),
    OPTIONS(RequestMethod.OPTIONS, javax.ws.rs.OPTIONS.class),
    TRACE(RequestMethod.TRACE, null);

    private static final Map<RequestMethod, OperationType> mapBySpringMvcRequestMethod = new HashMap<>();
    private static final Map<Class, OperationType> mapBySJaxrsAnnotationClass = new HashMap<>();

    static {
        for (OperationType type : OperationType.values()) {
            mapBySpringMvcRequestMethod.put(type.springMvcRequestMethod, type);
            mapBySJaxrsAnnotationClass.put(type.jaxrsVerbAnnotation, type);
        }
    }

    private final RequestMethod springMvcRequestMethod;
    private final Class jaxrsVerbAnnotation;

    OperationType(RequestMethod springMvcRequestMethod, Class jaxrsVerbAnnotation) {
        this.springMvcRequestMethod = springMvcRequestMethod;
        this.jaxrsVerbAnnotation = jaxrsVerbAnnotation;
    }

    public static OperationType from(final RequestMethod springMvcRequestMethod) {
        return mapBySpringMvcRequestMethod.get(springMvcRequestMethod);
    }

    public static OperationType from(final Class jaxrsVerbAnnotation) {
        return mapBySJaxrsAnnotationClass.get(jaxrsVerbAnnotation);
    }
}
