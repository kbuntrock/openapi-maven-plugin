package io.github.kbuntrock.resources.dto.optional.object;

import java.util.Optional;

/**
 * @author Kévin Buntrock
 */
public interface OptionalInterfaceDto {

	Optional<Optional<String>> getMyValue();

}
