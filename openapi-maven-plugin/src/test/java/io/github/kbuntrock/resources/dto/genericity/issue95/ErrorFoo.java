package io.github.kbuntrock.resources.dto.genericity.issue95;

import java.math.BigDecimal;

/**
 * This is the ErrorFoo declaration
 *
 * @param <T> a generic type
 */
public interface ErrorFoo<T> extends BaseValue<T, BigDecimal> {

}
