package io.github.kbuntrock.resources.endpoint.polymorphism;

import io.github.kbuntrock.resources.dto.polymorphism.base.AnimalDto;
import io.github.kbuntrock.resources.dto.polymorphism.subtype.CatDto;
import io.github.kbuntrock.resources.dto.polymorphism.subtype.DogDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Test a controller returning a base type which is extended by several other classes (polymorphism).
 *
 * @author Kevin Buntrock
 */
@RequestMapping("api")
public interface AnimalController {

	/**
	 * This endpoint returns an animal. The actual returned object may be any subtype of {@link AnimalDto}
	 * (e.g. {@link DogDto} or {@link CatDto}).
	 *
	 * @return the returned animal
	 */
	@GetMapping("animal")
	AnimalDto getAnimal();

}
