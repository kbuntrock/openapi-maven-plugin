package io.github.kbuntrock.reflection.annotation.spring;

import io.github.kbuntrock.reflection.annotation.MergedAnnotation;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;

public class SpringMergedAnnotations implements MergedAnnotations {

	private final Object mergedAnnotations;
	private final SpringMergeAnnotationsHelper helper;

	SpringMergedAnnotations(Object mergedAnnotations, SpringMergeAnnotationsHelper helper) {
		this.mergedAnnotations = mergedAnnotations;
		this.helper = helper;
	}

	@Override
	public MergedAnnotation get(String annotationType) {
		return helper.invokeWrapping("MergedAnnotations#get", () -> {
			Object result = helper.maMethodGet.invoke(mergedAnnotations, annotationType);
			return new SpringMergedAnnotation(result, helper);
		});
	}

}
