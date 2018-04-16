package com.chiricker.areas.home.utils.greeting;

public class Greeting {

    private GreetingStatus greetingStatus;

    public Greeting(GreetingStatus greetingStatus) {
        this.greetingStatus = greetingStatus;
    }

    public GreetingStatus getGreetingStatus() {
        return greetingStatus;
    }

    public void setGreetingStatus(GreetingStatus greetingStatus) {
        this.greetingStatus = greetingStatus;
    }
}