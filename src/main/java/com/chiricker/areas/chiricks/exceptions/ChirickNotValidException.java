package com.chiricker.areas.chiricks.exceptions;

public class ChirickNotValidException extends Exception {

    private static final String DEFAULT_MESSAGE = "Chirick is not valid";

    public ChirickNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public ChirickNotValidException(String message) {
        super(message);
    }
}