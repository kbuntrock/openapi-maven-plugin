package io.github.kbuntrock.configuration;

/**
 * How the classes annotated with {@code @JsonTypeInfo} and {@code @JsonSubTypes} are documented: as any other class (NONE), with
 * the discriminator on the parent and a sub-type extending it with "allOf" (ALL_OF), or with the parent being the "oneOf" of its
 * sub-types (ONE_OF).
 */
public enum JacksonPolymorphism {
	NONE,
	ALL_OF,
	ONE_OF
}
