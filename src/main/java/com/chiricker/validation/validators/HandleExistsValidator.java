package com.chiricker.validation.validators;

import com.chiricker.services.user.UserService;
import com.chiricker.validation.annotations.HandleExists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HandleExistsValidator implements ConstraintValidator<HandleExists, String> {

    private final UserService userService;

    @Autowired
    public HandleExistsValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String handle, ConstraintValidatorContext constraintValidatorContext) {
        return !this.userService.handleExists(handle);
    }
}
