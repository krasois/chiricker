package com.chiricker.areas.chiricks.models.view;

public class ChirickUserViewModel {

    private String handle;

    private String profilePicUrl;

    private String name;

    private String profileBiography;

    private String profileWebsiteUrl;

    private boolean isFollowing;

    public ChirickUserViewModel() {
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean following) {
        isFollowing = following;
    }
}
