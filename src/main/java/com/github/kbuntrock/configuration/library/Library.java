package com.github.kbuntrock.configuration.library;

import com.github.kbuntrock.MojoRuntimeException;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Buntrock
 */
public enum Library {
    SPRING_MVC(TagAnnotation.SPRING_MVC_REQUEST_MAPPING, TagAnnotation.SPRING_REST_CONTROLLER),
    JAXRS(TagAnnotation.JAXRS_PATH),
    VERTIGO(TagAnnotation.VERTIGO_PATH_PREFIX);

    private static Map<String, Library> nameMap = new HashMap<>();

    static {
        nameMap.put(SPRING_MVC.name().toLowerCase(), SPRING_MVC);
        nameMap.put(JAXRS.name().toLowerCase(), JAXRS);
        nameMap.put(VERTIGO.name().toLowerCase(), VERTIGO);
    }

    private Map<String, AnnotatedElement> annotationMap = new HashMap<>();
    private List<TagAnnotation> tagAnnotations = new ArrayList<>();

    Library(TagAnnotation... tagAnnotations) {
        for (TagAnnotation tagAnnotation : tagAnnotations) {
            this.tagAnnotations.add(tagAnnotation);
            annotationMap.put(tagAnnotation.getAnnotationClassName(), tagAnnotation.getAnnotatedElement());
        }
    }

    public List<TagAnnotation> getTagAnnotations() {
        return tagAnnotations;
    }

    public AnnotatedElement getByClassName(final String className) {
        AnnotatedElement toReturn = annotationMap.get(className);
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
        VERTIGO_PATH_PREFIX(PathPrefix.class),
        JAXRS_PATH(Path.class);

        private AnnotatedElement annotatedElement;

        private TagAnnotation(Class<?> clazz) {
            this.annotatedElement = clazz;
        }

        public AnnotatedElement getAnnotatedElement() {
            return annotatedElement;
        }

        public String getAnnotationClassName() {
            return ((Class) annotatedElement).getSimpleName();
        }
    }
}
