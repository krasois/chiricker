package com.chiricker.areas.admin.models;

import com.chiricker.areas.admin.validation.annotations.CollectionSize;
import com.chiricker.areas.users.validation.MIMEType;
import com.chiricker.areas.users.validation.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.Set;

import static com.chiricker.util.WebConstants.*;

@PasswordsMatch
public class EditUserBindingModel {

    private String id;

    @NotEmpty(message = NAME_EMPTY)
    @Size(min = NAME_MIN, max = NAME_MAX, message = NAME_LENGTH)
    private String name;

    @NotEmpty(message = HANDLE_EMPTY)
    @Size(min = HANDLE_MIN, max = HANDLE_MAX, message = HANDLE_LENGTH)
    @Pattern(regexp = HANDLE_PATTERN, message = HANDLE_PATTERN_MESSAGE)
    @HandleExists(isLogged = true, isAdmin = true)
    private String handle;

    @Password(min = PASSWORD_MIN, max = PASSWORD_MAX, nullable = true, message = PASSWORD_LENGTH)
    private String password;

    private String confirmPassword;

    @NotEmpty(message = EMAIL_EMPTY)
    @Email(regexp = EMAIL_PATTERN, message = EMAIL_INVALID)
    private String email;

    @CollectionSize(min = AUTHORITIES_MIN)
    private Set<String> authorities;

    private String profileBiography;

    @Website
    private String profileWebsiteUrl;

    private String profilePicUrl;

    @MultipartSize
    @MultipartType(supportedTypes = {MIMEType.PNG, MIMEType.JPEG, MIMEType.BMP})
    private MultipartFile profilePicture;

    public EditUserBindingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public String getProfileBiography() {
        return profileBiography;
    }

    public void setProfileBiography(String profileBiography) {
        this.profileBiography = profileBiography;
    }

    public String getProfileWebsiteUrl() {
        return profileWebsiteUrl;
    }

    public void setProfileWebsiteUrl(String profileWebsiteUrl) {
        this.profileWebsiteUrl = profileWebsiteUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public MultipartFile getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }
}
