package com.github.kbuntrock.configuration.library;

import com.github.kbuntrock.MojoRuntimeException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Buntrock
 */
public enum Library {
    SPRING_MVC(TagAnnotation.SPRING_MVC_REQUEST_MAPPING, TagAnnotation.SPRING_REST_CONTROLLER),
    JAXRS(TagAnnotation.JAXRS_PATH);

    private static Map<String, Library> nameMap = new HashMap<>();

    static {
        nameMap.put(SPRING_MVC.name().toLowerCase(), SPRING_MVC);
        nameMap.put(JAXRS.name().toLowerCase(), JAXRS);
    }

    private Map<String, Class<? extends Annotation>> annotationMap = new HashMap<>();
    private List<TagAnnotation> tagAnnotations = new ArrayList<>();

    Library(TagAnnotation... tagAnnotations) {
        for (TagAnnotation tagAnnotation : tagAnnotations) {
            this.tagAnnotations.add(tagAnnotation);
            annotationMap.put(tagAnnotation.getAnnotationClassName(), tagAnnotation.getAnnotattion());
        }
    }

    public List<TagAnnotation> getTagAnnotations() {
        return tagAnnotations;
    }

    public Class<? extends Annotation> getByClassName(final String className) {
        Class<? extends Annotation> toReturn = annotationMap.get(className);
        if (toReturn == null) {
            throw new MojoRuntimeException("There is no annotation corresponding to : " + className);
        }
        return toReturn;
    }

    public static Library getByName(final String name) {
        Library library = nameMap.get(name.toLowerCase());
        if (library == null) {
            throw new MojoRuntimeException("There is no library corresponding to : " + name);
        }
        return library;
    }

    public enum TagAnnotation {
        SPRING_MVC_REQUEST_MAPPING(RequestMapping.class),
        SPRING_REST_CONTROLLER(RestController.class),
        JAXRS_PATH(Path.class);

        private Class<? extends Annotation> annotatedElement;

        private TagAnnotation(Class<? extends Annotation> clazz) {
            this.annotatedElement = clazz;
        }

        public Class<? extends Annotation> getAnnotattion() {
            return annotatedElement;
        }

        public String getAnnotationClassName() {
            return ((Class) annotatedElement).getSimpleName();
        }
    }
}
