package com.chiricker.validation.annotations;

import com.chiricker.validation.validators.HandlePasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = HandlePasswordValidator.class)
public @interface HandlePasswordMatch {

    String message() default "Handle and password do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
