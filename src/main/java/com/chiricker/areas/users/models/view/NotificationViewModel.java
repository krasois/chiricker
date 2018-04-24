package com.chiricker.areas.users.models.view;

public class NotificationViewModel {

    private String id;

    private String userHandle;

    private String userName;

    private String chirickContent;

    public NotificationViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChirickContent() {
        return chirickContent;
    }

    public void setChirickContent(String chirickContent) {
        this.chirickContent = chirickContent;
    }
}
