package com.chiricker.users.validation.validators;

import com.chiricker.users.services.user.UserService;
import com.chiricker.users.validation.annotations.HandleExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HandleExistsValidator implements ConstraintValidator<HandleExists, String> {

    private final UserService userService;

    private boolean isLogged;

    @Autowired
    public HandleExistsValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(HandleExists constraintAnnotation) {
        this.isLogged = constraintAnnotation.isLogged();
    }

    @Override
    public boolean isValid(String handle, ConstraintValidatorContext constraintValidatorContext) {
        boolean handleExists = this.userService.handleExists(handle);
        if (!isLogged) {
            return !handleExists;
        }

        boolean isHandleUnchanged;
        String userHandle = SecurityContextHolder.getContext().getAuthentication().getName();
        isHandleUnchanged = userHandle.equals(handle);

        return isHandleUnchanged || !handleExists;
    }
}