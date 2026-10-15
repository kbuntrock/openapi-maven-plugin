package io.github.kbuntrock.resources.dto.jackson.polymorphism;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "petType")
@JsonSubTypes({
		@JsonSubTypes.Type(Cat.class),
		@JsonSubTypes.Type(value = Dog.class, name = "dog")
})
public abstract class Pet {

	@NotNull
	public String name;
}
