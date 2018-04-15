package com.chiricker.areas.users.validation.annotations;

import com.chiricker.areas.users.validation.validators.WebsiteValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = { WebsiteValidator.class})
public @interface Website {

    String regexp() default "^(http:\\/\\/|https:\\/\\/).*?";

    boolean nullable() default true;

    String message() default "Website is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
