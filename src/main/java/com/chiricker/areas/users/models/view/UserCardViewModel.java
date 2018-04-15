package com.chiricker.areas.users.models.view;

public class UserCardViewModel {

    private String name;

    private String handle;

    private String biography;

    private String profilePicUrl;

    private String websiteUrl;

    private boolean canFollow;

    public UserCardViewModel() {
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

    public boolean isCanFollow() {
        return canFollow;
    }

    public void setCanFollow(boolean canFollow) {
        this.canFollow = canFollow;
    }
}
