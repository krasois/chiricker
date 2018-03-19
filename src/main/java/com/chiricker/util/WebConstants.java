package com.chiricker.util;

public final class WebConstants {

    public static final String NAME_EMPTY = "Name field cannot be empty";
    public static final String NAME_LENGTH = "Name must be between 1 and 50 characters long";
    public static final String HANDLE_EMPTY = "Handle field cannot be empty";
    public static final String HANDLE_LENGTH = "Handle must be between 5 and 40 characters long";
    public static final String EMAIL_EMPTY = "Email field cannot be empty";
    public static final String PASSWORD_EMPTY = "Password field cannot be empty";
    public static final String EMAIL_INVALID = "Email must be valid";
    public static final String PASSWORD_LENGTH = "Password must be between 6 and 25 characters long";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final int NAME_MIN = 1;
    public static final int NAME_MAX = 50;
    public static final int HANDLE_MIN = 5;
    public static final int HANDLE_MAX = 40;
    public static final int PASSWORD_MIN = 6;
    public static final int PASSWORD_MAX = 25;

    private WebConstants() {}
}