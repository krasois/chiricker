package com.chiricker.users.validation.validators;

import com.chiricker.users.models.binding.UserEditBindingModel;
import com.chiricker.users.validation.annotations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchOrIsEmptyValidator implements ConstraintValidator<PasswordsMatch, UserEditBindingModel> {

    private static final String EMPTY_STRING = "";

    @Override
    public boolean isValid(UserEditBindingModel bindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return bindingModel.getPassword().equals(EMPTY_STRING) || bindingModel.getPassword().equals(bindingModel.getConfirmPassword());
    }
}