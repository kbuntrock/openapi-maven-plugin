package io.github.kbuntrock.configuration.library;

public enum JaxrsHttpVerb {
	GET(javax.ws.rs.GET.class),
	PUT(javax.ws.rs.PUT.class),
	POST(javax.ws.rs.POST.class),
	DELETE(javax.ws.rs.DELETE.class),
	PATCH(javax.ws.rs.PATCH.class),
	OPTIONS(javax.ws.rs.OPTIONS.class),
	HEAD(javax.ws.rs.HEAD.class);

	private final Class annotationClass;

	JaxrsHttpVerb(Class annotationClass) {
		this.annotationClass = annotationClass;
	}

	public Class getAnnotationClass() {
		return annotationClass;
	}
}
