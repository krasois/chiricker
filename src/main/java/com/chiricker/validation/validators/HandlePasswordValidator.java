package com.chiricker.validation.validators;

import com.chiricker.models.binding.UserLoginBindingModel;
import com.chiricker.services.user.UserService;
import com.chiricker.validation.annotations.HandlePasswordMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HandlePasswordValidator implements ConstraintValidator<HandlePasswordMatch, UserLoginBindingModel> {

    private final UserService userService;

    @Autowired
    public HandlePasswordValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(UserLoginBindingModel model, ConstraintValidatorContext constraintValidatorContext) {
        return this.userService.handleAndPasswordMatch(model.getHandle(), model.getPassword());
    }
}