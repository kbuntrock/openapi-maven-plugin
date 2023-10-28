package io.github.kbuntrock.resources.dto.genericity.issue95;

import java.util.Optional;

/**
 * This is the BaseValue declaration
 *
 * @param <T>   a first generic type
 * @param <VAL> another generic type
 */
public interface BaseValue<T, VAL> {

	/**
	 * Get the value!
	 *
	 * @return the returned value
	 */
	VAL getValue();

	/**
	 * Get the unit!
	 *
	 * @return the optional returned unit
	 */
	Optional<T> getUnit();
}
