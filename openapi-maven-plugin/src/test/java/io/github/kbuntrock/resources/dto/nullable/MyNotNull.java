package io.github.kbuntrock.resources.dto.nullable;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyNotNull {
}
