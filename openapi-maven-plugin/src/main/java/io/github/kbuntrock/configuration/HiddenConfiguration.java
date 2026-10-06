package io.github.kbuntrock.configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class HiddenConfiguration {
	private static final String DEFAULT_SWAGGER_HIDDEN_ANNOTATION = "io.swagger.v3.oas.annotations.Hidden";
	private static final String DEFAULT_SPRINGFOX_IGNORE = "springfox.documentation.annotations.ApiIgnore";
	private static final String DEFAULT_MICROPROFILE_HIDDEN = "org.eclipse.microprofile.openapi.annotations.Hidden";

	private final List<String> hiddenAnnotations;

	public HiddenConfiguration(final CommonApiConfiguration commonApiConfiguration) {
		hiddenAnnotations = new ArrayList<>();
		if(commonApiConfiguration.hiddenAnnotations != null) {
			hiddenAnnotations.addAll(commonApiConfiguration.hiddenAnnotations);
		} else {
			hiddenAnnotations.add(DEFAULT_SWAGGER_HIDDEN_ANNOTATION);
			hiddenAnnotations.add(DEFAULT_SPRINGFOX_IGNORE);
			hiddenAnnotations.add(DEFAULT_MICROPROFILE_HIDDEN);
		}
	}

	private List<String> getHiddenAnnotations() {
		return hiddenAnnotations;
	}

	public boolean hasHiddenAnnotations(final List<Annotation> annotations) {
		return annotations.stream()
			.map(annotation -> annotation.annotationType().getName())
			.anyMatch(name -> getHiddenAnnotations().contains(name));
	}
}