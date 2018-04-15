package com.chiricker.areas.users.validation.validators;

import com.chiricker.areas.users.validation.annotations.Website;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WebsiteValidator implements ConstraintValidator<Website, String> {

    private boolean nullable;
    private String regexp;

    @Override
    public void initialize(Website constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String website, ConstraintValidatorContext constraintValidatorContext) {
        if (nullable && website.length() < 1) return true;

        return website.matches(regexp);
    }
}
