package com.chiricker.areas.users.models.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://dl.dropboxusercontent.com/s/y7i72l1yblmfydj/defaultProfileImage.png";

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(name = "biography", columnDefinition="TEXT")
    private String biography;

    @Column(name = "profile_picture_url")
    private String profilePicUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    public Profile() {
    }

    @PrePersist
    public void onCreate() {
        this.profilePicUrl = DEFAULT_PROFILE_PICTURE_URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}
