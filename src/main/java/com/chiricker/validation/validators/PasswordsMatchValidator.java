package com.chiricker.validation.validators;

import com.chiricker.models.binding.UserRegisterBindingModel;
import com.chiricker.validation.annotations.Password;
import com.chiricker.validation.annotations.PasswordsMatch;

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
        Field[] fields = bindingModel.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields)
                .filter(f -> f.isAnnotationPresent(Password.class))
                .collect(Collectors.toList());

        fieldList.forEach(f -> f.setAccessible(true));
        Set<String> values = fieldList.stream()
                .map(f -> {
                    try {
                        return (String) f.get(bindingModel);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .collect(Collectors.toSet());

        return values.size() == 1;
    }
}
