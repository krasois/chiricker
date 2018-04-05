package com.chiricker.users.exceptions;

public class UserNotFound extends Exception{

    private static final String DEFAULT_MESSAGE = "User was not found";

    public UserNotFound() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFound(String message) {
        super(message);
    }
}