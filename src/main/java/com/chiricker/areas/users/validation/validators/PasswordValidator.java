package com.chiricker.areas.users.validation.validators;

import com.chiricker.areas.users.validation.annotations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final String EMPTY_STRING = "";

    private int min;
    private int max;
    private boolean nullable;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (nullable && s.equals(EMPTY_STRING)) return true;
        return s.length() >= this.min && s.length() <= this.max;
    }
}
