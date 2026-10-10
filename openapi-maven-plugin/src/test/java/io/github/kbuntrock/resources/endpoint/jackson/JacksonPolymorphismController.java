package io.github.kbuntrock.resources.endpoint.jackson;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.polymorphism.Pet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(Constants.BASE_API + "/pets")
@RestController
public interface JacksonPolymorphismController {

	@GetMapping
	Pet getPet();
}
