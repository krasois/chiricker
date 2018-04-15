package com.chiricker.areas.users.models.view;

public class FollowerViewModel {

    private String name;

    private String handle;

    private boolean isFollowed;

    private boolean isSelf;

    private String profileBiography;

    private String profilePicUrl;

    public FollowerViewModel() {
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

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getProfileBiography() {
        return profileBiography;
    }

    public void setProfileBiography(String profileBiography) {
        this.profileBiography = profileBiography;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
