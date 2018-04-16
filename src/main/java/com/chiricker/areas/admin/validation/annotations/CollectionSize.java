package com.chiricker.areas.admin.validation.annotations;

import com.chiricker.areas.admin.validation.validators.CollectionSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CollectionSizeValidator.class)
public @interface CollectionSize {

    int min() default 0;

    String message() default "Invalid collection size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}