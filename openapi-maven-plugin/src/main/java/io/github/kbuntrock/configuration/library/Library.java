package io.github.kbuntrock.configuration.library;

import io.github.kbuntrock.MojoRuntimeException;
import io.github.kbuntrock.configuration.ApiConfiguration;
import io.github.kbuntrock.configuration.library.reader.AstractLibraryReader;
import io.github.kbuntrock.configuration.library.reader.ClassLoaderUtils;
import io.github.kbuntrock.configuration.library.reader.JakartaRsReader;
import io.github.kbuntrock.configuration.library.reader.JavaxRsReader;
import io.github.kbuntrock.configuration.library.reader.SpringMvcReader;
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
	JAVAX_RS(TagAnnotation.JAVAX_RS_PATH),
	JAKARTA_RS(TagAnnotation.JAKARTA_RS_PATH);

	private static final Map<String, Library> nameMap = new HashMap<>();

	static {
		nameMap.put(SPRING_MVC.name().toLowerCase(), SPRING_MVC);
		nameMap.put(JAVAX_RS.name().toLowerCase(), JAVAX_RS);
		nameMap.put(JAKARTA_RS.name().toLowerCase(), JAKARTA_RS);
	}

	private final List<TagAnnotation> tagAnnotations = new ArrayList<>();

	Library(final TagAnnotation... tagAnnotations) {
		for(final TagAnnotation tagAnnotation : tagAnnotations) {
			this.tagAnnotations.add(tagAnnotation);
		}
	}

	public static Library getByName(final String name) {
		if("JAXRS".equals(name.toUpperCase())) {
			// Small bypass for javax-rs common name
			return Library.JAVAX_RS;
		}
		final Library library = nameMap.get(name.toLowerCase());
		if(library == null) {
			throw new MojoRuntimeException("There is no library corresponding to : " + name);
		}
		return library;
	}

	public List<TagAnnotation> getTagAnnotations() {
		return tagAnnotations;
	}

	public Class<? extends Annotation> getByClassName(final String className) {
		return ClassLoaderUtils.getByNameRuntimeEx(className);
	}

	public AstractLibraryReader createReader(final ApiConfiguration apiConfiguration) {
		switch(this) {
			case JAVAX_RS:
				return new JavaxRsReader(apiConfiguration);
			case JAKARTA_RS:
				return new JakartaRsReader(apiConfiguration);
			case SPRING_MVC:
				return new SpringMvcReader(apiConfiguration);
			default:
				throw new MojoRuntimeException(this.name() + " library not handled yet.");
		}
	}
}
