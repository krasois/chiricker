package com.chiricker.models.binding;

import com.chiricker.validation.annotations.HandlePasswordMatch;

@HandlePasswordMatch
public class UserLoginBindingModel {

    private String handle;

    private String password;

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
