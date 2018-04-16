package com.chiricker.areas.users.validation.validators;

import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.areas.users.validation.annotations.HandleExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HandleExistsValidator implements ConstraintValidator<HandleExists, String> {

    private final UserService userService;

    private boolean isLogged;
    private boolean isAdmin;

    @Autowired
    public HandleExistsValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(HandleExists constraintAnnotation) {
        this.isLogged = constraintAnnotation.isLogged();
        this.isAdmin = constraintAnnotation.isAdmin();
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

        if (isAdmin) {
            boolean isAdminAction = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isHandleUnchanged && isAdminAction) isHandleUnchanged = true;
        }

        return isHandleUnchanged || !handleExists;
    }
}