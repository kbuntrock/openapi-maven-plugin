package io.github.kbuntrock.resources.dto.genericity.issue95;

/**
 * This is the ErrorDto declaration
 */
public interface ErrorDto {

	/**
	 * Get the foo
	 *
	 * @return a string type ErrorFoo
	 */
	ErrorFoo<String> getFoo();
}
