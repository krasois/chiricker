package com.chiricker.areas.chiricks.models.view;

public class TimelinePostViewModel {

    private String postTypeValue;

    private String userHandle;

    private ChirickViewModel chirick;

    public TimelinePostViewModel() {
    }

    public String getPostTypeValue() {
        return postTypeValue;
    }

    public void setPostTypeValue(String postTypeValue) {
        this.postTypeValue = postTypeValue;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public ChirickViewModel getChirick() {
        return chirick;
    }

    public void setChirick(ChirickViewModel chirick) {
        this.chirick = chirick;
    }
}