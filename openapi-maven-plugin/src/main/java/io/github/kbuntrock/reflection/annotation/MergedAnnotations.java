package io.github.kbuntrock.reflection.annotation;

public class MergedAnnotations {

	private final Object mergedAnnotations;
	private final MergeAnnotationsHelper helper;

	MergedAnnotations(Object mergedAnnotations, MergeAnnotationsHelper helper) {
		this.mergedAnnotations = mergedAnnotations;
		this.helper = helper;
	}

	public MergedAnnotation get(String annotationType) {
		return helper.invokeWrapping("MergedAnnotations#get", () -> {
			Object result = helper.maMethodGet.invoke(mergedAnnotations, annotationType);
			return new MergedAnnotation(result, helper);
		});
	}

	public boolean isPresent(String annotationType) {
		return helper.invokeWrapping("MergedAnnotations#isPresent",
			() -> (boolean) helper.maMethodIsPresent.invoke(mergedAnnotations, annotationType));
	}

}
