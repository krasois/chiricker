package com.chiricker.users.models.binding;

public class UserLoginBindingModel {

    private String handle;

    private String password;

    public UserLoginBindingModel() {
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
