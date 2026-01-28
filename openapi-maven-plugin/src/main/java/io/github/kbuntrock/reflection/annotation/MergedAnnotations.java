package io.github.kbuntrock.reflection.annotation;

public interface MergedAnnotations {

	MergedAnnotation get(String annotationType);

	boolean isPresent(String annotationType);
}
