package com.chiricker.areas.home.utils.greeting;

public enum GreetingStatus {

    GENERAL("Hello, %s!", "greeting-general"),
    MORNING("Good morning, %s!", "greeting-morning"),
    NOON("Bon Appetit, %s!", "greeting-noon"),
    AFTERNOON("Good afternoon, %s!", "greeting-afternoon"),
    EVENING("Good evening, %s!", "greeting-evening"),
    NIGHT("Hello fellow night owl!", "greeting-night");

    private final String message;
    private final String htmlClass;

    GreetingStatus(String message, String htmlClass) {
        this.message = message;
        this.htmlClass = htmlClass;
    }

    public String getMessage() {
        return this.message;
    }

    public String getHtmlClass() {
        return htmlClass;
    }
}