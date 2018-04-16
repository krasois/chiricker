package com.chiricker.areas.admin.validation.validators;

import com.chiricker.areas.admin.models.EditUserBindingModel;
import com.chiricker.areas.users.validation.annotations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidatorAdmin implements ConstraintValidator<PasswordsMatch, EditUserBindingModel> {
    @Override
    public boolean isValid(EditUserBindingModel editUserBindingModel, ConstraintValidatorContext constraintValidatorContext) {
        return editUserBindingModel.getPassword().equals(editUserBindingModel.getConfirmPassword());
    }
}
