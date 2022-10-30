package io.github.kbuntrock.configuration.library;

import io.github.kbuntrock.MojoRuntimeException;
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

	private static final Map<String, Library> nameMap = new HashMap<>();

	static {
		nameMap.put(SPRING_MVC.name().toLowerCase(), SPRING_MVC);
		nameMap.put(JAXRS.name().toLowerCase(), JAXRS);
	}

	private final Map<String, Class<? extends Annotation>> annotationMap = new HashMap<>();
	private final List<TagAnnotation> tagAnnotations = new ArrayList<>();

	Library(TagAnnotation... tagAnnotations) {
		for(TagAnnotation tagAnnotation : tagAnnotations) {
			this.tagAnnotations.add(tagAnnotation);
			annotationMap.put(tagAnnotation.getAnnotationClassName(), tagAnnotation.getAnnotattion());
		}
	}

	public static Library getByName(final String name) {
		Library library = nameMap.get(name.toLowerCase());
		if(library == null) {
			throw new MojoRuntimeException("There is no library corresponding to : " + name);
		}
		return library;
	}

	public List<TagAnnotation> getTagAnnotations() {
		return tagAnnotations;
	}

	public Class<? extends Annotation> getByClassName(final String className) {
		Class<? extends Annotation> toReturn = annotationMap.get(className);
		if(toReturn == null) {
			throw new MojoRuntimeException("There is no annotation corresponding to : " + className);
		}
		return toReturn;
	}
}
