package com.chiricker.general.controllers;

import com.chiricker.chiricks.exceptions.ChirickNotValidException;
import com.chiricker.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionController extends BaseController{

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView entityNotFound(UserNotFoundException e) {
        return this.view("error/not-found-entity", "errorMessage", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MultipartException.class)
    public ModelAndView fileTooBig() {
        return this.view("error/file-too-big");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChirickNotValidException.class)
    public @ResponseBody String chirick(ChirickNotValidException e) {
        return e.getMessage();
    }
}