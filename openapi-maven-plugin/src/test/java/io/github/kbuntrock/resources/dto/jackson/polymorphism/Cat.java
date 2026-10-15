package io.github.kbuntrock.resources.dto.jackson.polymorphism;

import javax.validation.constraints.NotNull;

public class Cat extends Pet {

	@NotNull
	public HuntingSkill huntingSkill;
}
