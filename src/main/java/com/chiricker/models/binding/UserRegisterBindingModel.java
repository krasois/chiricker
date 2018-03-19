package com.chiricker.models.binding;

import com.chiricker.validation.annotations.HandleExists;
import com.chiricker.validation.annotations.Password;
import com.chiricker.validation.annotations.PasswordsMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.chiricker.util.WebConstants.*;

@PasswordsMatch
public class UserRegisterBindingModel {

    @NotEmpty(message = NAME_EMPTY)
    @Size(min = NAME_MIN, max = NAME_MAX, message = NAME_LENGTH)
    private String name;

    @NotEmpty(message = HANDLE_EMPTY)
    @Size(min = HANDLE_MIN, max = HANDLE_MAX, message = HANDLE_LENGTH)
    @HandleExists
    private String handle;

    @NotEmpty(message = PASSWORD_EMPTY)
    @Size(min = PASSWORD_MIN, max = PASSWORD_MAX, message = PASSWORD_LENGTH)
    @Password
    private String password;

    @Password
    private String confirmPassword;

    @NotEmpty(message = EMAIL_EMPTY)
    @Email(regexp = EMAIL_PATTERN, message = EMAIL_INVALID)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
