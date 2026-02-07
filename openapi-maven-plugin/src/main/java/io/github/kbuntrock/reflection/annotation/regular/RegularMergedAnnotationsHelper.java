package io.github.kbuntrock.reflection.annotation.regular;

import io.github.kbuntrock.configuration.library.reader.ClassLoaderHelper;
import io.github.kbuntrock.reflection.annotation.MergeAnnotationsHelper;
import io.github.kbuntrock.reflection.annotation.MergedAnnotations;

import java.lang.reflect.AnnotatedElement;

public class RegularMergedAnnotationsHelper implements MergeAnnotationsHelper {

	private final ClassLoaderHelper classLoaderHelper;

	public RegularMergedAnnotationsHelper(final ClassLoaderHelper classLoaderHelper) {
		this.classLoaderHelper = classLoaderHelper;
	}

	@Override
	public MergedAnnotations from(AnnotatedElement element) {
		return new RegularMergeAnnotations(classLoaderHelper, element);
	}
}
