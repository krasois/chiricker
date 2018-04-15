package com.chiricker.areas.users.validation.annotations;

import com.chiricker.areas.users.validation.validators.PasswordsMatchOrIsEmptyValidator;
import com.chiricker.areas.users.validation.validators.PasswordsMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = { PasswordsMatchValidator.class, PasswordsMatchOrIsEmptyValidator.class})
public @interface PasswordsMatch {

    String message() default "Passwords do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
