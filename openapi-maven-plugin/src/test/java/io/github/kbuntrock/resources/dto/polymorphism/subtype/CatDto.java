package io.github.kbuntrock.resources.dto.polymorphism.subtype;

import io.github.kbuntrock.resources.dto.polymorphism.mammal.MammalDto;

public class CatDto extends MammalDto {

	private boolean indoor;

	public boolean isIndoor() {
		return indoor;
	}
}
