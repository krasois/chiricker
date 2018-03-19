package com.chiricker.validation.annotations;

import com.chiricker.validation.validators.HandleExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = HandleExistsValidator.class)
public @interface HandleExists {

    String message() default "User with this handle already exists, try another one";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
