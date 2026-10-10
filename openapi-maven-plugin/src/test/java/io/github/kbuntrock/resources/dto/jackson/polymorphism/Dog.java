package io.github.kbuntrock.resources.dto.jackson.polymorphism;

import javax.validation.constraints.NotNull;

public class Dog extends Pet {

	@NotNull
	public Integer packSize;
}
