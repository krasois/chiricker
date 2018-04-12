package com.chiricker.chiricks.exceptions;

public class ChirickNotFoundException extends Exception {

    private static final String DEFAULT_MESSAGE = "The chirick were looking for is nowhere to be found";

    public ChirickNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ChirickNotFoundException(String message) {
        super(message);
    }
}