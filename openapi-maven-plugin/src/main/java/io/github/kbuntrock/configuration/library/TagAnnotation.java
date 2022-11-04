package io.github.kbuntrock.configuration.library;

/**
 * @author Kevin Buntrock
 */
public enum TagAnnotation {
	SPRING_MVC_REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping"),
	SPRING_REST_CONTROLLER("org.springframework.web.bind.annotation.RestController"),
	JAVAX_RS_PATH("javax.ws.rs.Path"),
	JAKARTA_RS_PATH("jakarta.ws.rs.Path");

	private final String annotatedElement;

	TagAnnotation(final String annotatedElement) {
		this.annotatedElement = annotatedElement;
	}


	public String getAnnotationClassName() {
		return annotatedElement;
	}
}
