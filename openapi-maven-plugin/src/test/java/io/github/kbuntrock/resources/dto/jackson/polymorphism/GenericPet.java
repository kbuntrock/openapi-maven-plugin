package io.github.kbuntrock.resources.dto.jackson.polymorphism;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "petType")
@JsonSubTypes({
		@JsonSubTypes.Type(GenericCat.class),
		@JsonSubTypes.Type(value = GenericDog.class, name = "dog")
})
public abstract class GenericPet<T> {

	@NotNull
	public T attribute;
}
