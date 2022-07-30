package com.github.kbuntrock.configuration.library;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;

/**
 * @author Kevin Buntrock
 */
public enum TagAnnotation {
    SPRING_MVC_REQUEST_MAPPING(RequestMapping.class),
    SPRING_REST_CONTROLLER(RestController.class),
    JAXRS_PATH(Path.class);

    private Class<? extends Annotation> annotatedElement;

    TagAnnotation(Class<? extends Annotation> clazz) {
        this.annotatedElement = clazz;
    }

    public Class<? extends Annotation> getAnnotattion() {
        return annotatedElement;
    }

    public String getAnnotationClassName() {
        return ((Class) annotatedElement).getSimpleName();
    }
}
