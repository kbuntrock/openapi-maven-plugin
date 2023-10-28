package io.github.kbuntrock.resources.dto.genericity.issue95;

import java.util.Optional;

public interface BaseValue<T, VAL> {

	VAL getValue();

	Optional<T> getUnit();
}
