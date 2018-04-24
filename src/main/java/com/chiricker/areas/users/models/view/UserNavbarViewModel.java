package com.chiricker.areas.users.models.view;

public class UserNavbarViewModel {

    private String name;

    private String handle;

    private String profilePicUrl;

    private long notificationsCount;

    public UserNavbarViewModel() {
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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public long getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(long notificationsCount) {
        this.notificationsCount = notificationsCount;
    }
}
