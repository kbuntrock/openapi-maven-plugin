package io.github.kbuntrock.configuration.library;

import java.lang.annotation.Annotation;
import javax.ws.rs.Path;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kevin Buntrock
 */
public enum TagAnnotation {
	SPRING_MVC_REQUEST_MAPPING(RequestMapping.class),
	SPRING_REST_CONTROLLER(RestController.class),
	JAVAX_RS_PATH(Path.class),
	JAKARTA_RS_PATH(jakarta.ws.rs.Path.class);

	private final Class<? extends Annotation> annotatedElement;

	TagAnnotation(final Class<? extends Annotation> clazz) {
		this.annotatedElement = clazz;
	}

	public Class<? extends Annotation> getAnnotattion() {
		return annotatedElement;
	}

	public String getAnnotationClassName() {
		return annotatedElement.getSimpleName();
	}
}
