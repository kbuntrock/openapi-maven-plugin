package io.github.kbuntrock.resources.dto.polymorphism.subtype;

import io.github.kbuntrock.resources.dto.polymorphism.mammal.MammalDto;

public class DogDto extends MammalDto {

	private String breed;

	public String getBreed() {
		return breed;
	}
}
