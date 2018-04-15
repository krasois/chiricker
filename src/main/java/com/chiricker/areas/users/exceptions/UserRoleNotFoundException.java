package com.chiricker.areas.users.exceptions;

public class UserRoleNotFoundException extends Exception {

    private static final String DEFAULT_MESSAGE = "Role was not found";

    public UserRoleNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
