package io.github.kbuntrock.reflection.annotation;

public class SpringMergedAnnotations implements MergedAnnotations {

	private final Object mergedAnnotations;
	private final MergeAnnotationsHelper helper;

	SpringMergedAnnotations(Object mergedAnnotations, MergeAnnotationsHelper helper) {
		this.mergedAnnotations = mergedAnnotations;
		this.helper = helper;
	}

	@Override
	public MergedAnnotation get(String annotationType) {
		return helper.invokeWrapping("MergedAnnotations#get", () -> {
			Object result = helper.maMethodGet.invoke(mergedAnnotations, annotationType);
			return new SpringMergedAnnotation²(result, helper);
		});
	}

	@Override
	public boolean isPresent(String annotationType) {
		return helper.invokeWrapping("MergedAnnotations#isPresent",
			() -> (boolean) helper.maMethodIsPresent.invoke(mergedAnnotations, annotationType));
	}

}
