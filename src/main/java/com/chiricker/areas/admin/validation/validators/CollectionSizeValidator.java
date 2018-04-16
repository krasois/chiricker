package com.chiricker.areas.admin.validation.validators;

import com.chiricker.areas.admin.validation.annotations.CollectionSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

public class CollectionSizeValidator implements ConstraintValidator<CollectionSize, Collection<?>> {

    private int minSize;

    @Override
    public void initialize(CollectionSize constraintAnnotation) {
        this.minSize = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext constraintValidatorContext) {
        return collection != null && collection.size() >= this.minSize;
    }
}