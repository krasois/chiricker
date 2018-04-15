package com.chiricker.areas.users.validation.annotations;

import com.chiricker.areas.users.validation.validators.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = { PasswordValidator.class})
public @interface Password {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    boolean nullable() default false;

    String message() default "Passwords length is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
