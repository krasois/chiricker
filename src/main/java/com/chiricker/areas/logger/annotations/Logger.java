package com.chiricker.areas.logger.annotations;

import com.chiricker.areas.logger.models.entities.enums.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Logger {

    Operation operation();

    Class<?> entity();
}