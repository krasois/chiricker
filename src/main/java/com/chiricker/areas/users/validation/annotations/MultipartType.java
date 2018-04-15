package com.chiricker.areas.users.validation.annotations;

import com.chiricker.areas.users.validation.MIMEType;
import com.chiricker.areas.users.validation.validators.MultipartFileTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = { MultipartFileTypeValidator.class})
public @interface MultipartType {

    MIMEType[] supportedTypes();

    String message() default "File type is not supported.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}