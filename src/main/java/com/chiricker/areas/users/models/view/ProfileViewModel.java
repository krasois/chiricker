package com.chiricker.areas.users.models.view;

public class ProfileViewModel {

    private String handle;

    private String profilePicUrl;

    private String name;

    private String profileBiography;

    private String profileWebsiteUrl;

    private int chiricksCount;

    private int followingCount;

    private int followersCount;

    private boolean isFollowing;

    public ProfileViewModel() {
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

    public int getChiricksCount() {
        return chiricksCount;
    }

    public void setChiricksCount(int chiricksCount) {
        this.chiricksCount = chiricksCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
