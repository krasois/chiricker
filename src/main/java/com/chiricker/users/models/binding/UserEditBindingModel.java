package com.chiricker.users.models.binding;

import com.chiricker.users.validation.annotations.HandleExists;
import com.chiricker.users.validation.annotations.Password;
import com.chiricker.users.validation.annotations.PasswordsMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.chiricker.util.WebConstants.*;

@PasswordsMatch
public class UserEditBindingModel {

    @NotEmpty(message = NAME_EMPTY)
    @Size(min = NAME_MIN, max = NAME_MAX, message = NAME_LENGTH)
    private String name;

    @NotEmpty(message = HANDLE_EMPTY)
    @Size(min = HANDLE_MIN, max = HANDLE_MAX, message = HANDLE_LENGTH)
    @HandleExists(isLogged = true)
    private String handle;

    @Password(min = PASSWORD_MIN, max = PASSWORD_MAX, nullable = true, message = PASSWORD_LENGTH)
    private String password;

    private String confirmPassword;

    @NotEmpty(message = EMAIL_EMPTY)
    @Email(regexp = EMAIL_PATTERN, message = EMAIL_INVALID)
    private String email;

    private String biography;

    private String websiteUrl;

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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}