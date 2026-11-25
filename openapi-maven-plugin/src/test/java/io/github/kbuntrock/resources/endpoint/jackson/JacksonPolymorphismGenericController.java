package io.github.kbuntrock.resources.endpoint.jackson;

import io.github.kbuntrock.resources.Constants;
import io.github.kbuntrock.resources.dto.jackson.polymorphism.GenericPet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(Constants.BASE_API + "/generic-pets")
@RestController
public interface JacksonPolymorphismGenericController {

	@GetMapping
	List<GenericPet<?>> getPets();
}
