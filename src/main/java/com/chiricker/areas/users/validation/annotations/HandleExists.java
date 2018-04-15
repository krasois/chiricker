package com.chiricker.areas.users.validation.annotations;

import com.chiricker.areas.users.validation.validators.HandleExistsValidator;

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

    boolean isLogged() default false;

    String message() default "User with this handle already exists, try another one";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
