package com.chiricker.users.validation.validators;

import com.chiricker.users.models.binding.UserRegisterBindingModel;
import com.chiricker.users.validation.annotations.Password;
import com.chiricker.users.validation.annotations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, UserRegisterBindingModel> {

    @Override
    public boolean isValid(UserRegisterBindingModel bindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return bindingModel.getPassword().equals(bindingModel.getConfirmPassword());
    }
}
