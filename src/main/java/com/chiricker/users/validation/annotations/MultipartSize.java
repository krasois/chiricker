package com.chiricker.users.validation.annotations;

import com.chiricker.users.validation.validators.MultipartFileSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = { MultipartFileSizeValidator.class})
public @interface MultipartSize {

    long maxSize() default 1048576;

    String message() default "File size is too big.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
