package com.chiricker.areas.users.validation.validators;

import com.chiricker.areas.users.validation.annotations.MultipartSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MultipartFileSizeValidator implements ConstraintValidator<MultipartSize, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(MultipartSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        return multipartFile.getSize() < this.maxSize;
    }
}