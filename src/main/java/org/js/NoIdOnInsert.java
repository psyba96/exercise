package org.js;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoIdOnInsertValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoIdOnInsert {
    String message() default "ID should not be provided for insert operations";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
