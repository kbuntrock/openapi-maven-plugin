package io.github.kbuntrock.resources.endpoint.jaxrs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kévin Buntrock
 */
// TODO : test retention class (or even source)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResponseType {

	Class value();

}
