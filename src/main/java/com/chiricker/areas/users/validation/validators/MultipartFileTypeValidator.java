package com.chiricker.areas.users.validation.validators;

import com.chiricker.areas.users.validation.MIMEType;
import com.chiricker.areas.users.validation.annotations.MultipartType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MultipartFileTypeValidator implements ConstraintValidator<MultipartType, MultipartFile> {

    private MIMEType[] supportedTypes;

    @Override
    public void initialize(MultipartType constraintAnnotation) {
        this.supportedTypes = constraintAnnotation.supportedTypes();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile.getSize() < 1) return true;
        for (MIMEType supportedType : supportedTypes) {
            if (supportedType.getMimeType().equals(multipartFile.getContentType())) return true;
        }
        return false;
    }
}