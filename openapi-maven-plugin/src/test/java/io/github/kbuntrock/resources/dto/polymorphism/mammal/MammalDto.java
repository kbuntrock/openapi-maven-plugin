package io.github.kbuntrock.resources.dto.polymorphism.mammal;

import io.github.kbuntrock.resources.dto.polymorphism.base.AnimalDto;

public class MammalDto implements AnimalDto {
	private String name;
	private boolean adult;

	@Override
	public String getName() {
		return this.name;
	}

	public boolean isAdult() {
		return adult;
	}
}
