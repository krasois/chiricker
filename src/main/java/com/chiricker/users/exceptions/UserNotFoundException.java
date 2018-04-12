package com.chiricker.users.exceptions;

public class UserNotFoundException extends Exception{

    private static final String DEFAULT_MESSAGE = "User was not found";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}