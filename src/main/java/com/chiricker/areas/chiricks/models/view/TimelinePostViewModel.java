package com.chiricker.areas.chiricks.models.view;

public class TimelinePostViewModel {

    private String postTypeValue;

    private String fromHandle;

    private ChirickViewModel chirick;

    public TimelinePostViewModel() {
    }

    public String getPostTypeValue() {
        return postTypeValue;
    }

    public void setPostTypeValue(String postTypeValue) {
        this.postTypeValue = postTypeValue;
    }

    public String getFromHandle() {
        return fromHandle;
    }

    public void setFromHandle(String fromHandle) {
        this.fromHandle = fromHandle;
    }

    public ChirickViewModel getChirick() {
        return chirick;
    }

    public void setChirick(ChirickViewModel chirick) {
        this.chirick = chirick;
    }
}