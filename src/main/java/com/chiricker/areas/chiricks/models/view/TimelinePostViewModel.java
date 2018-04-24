package com.chiricker.areas.chiricks.models.view;

public class TimelinePostViewModel {

    private String postTypeValue;

    private String posterHandle;

    private ChirickViewModel chirick;

    public TimelinePostViewModel() {
    }

    public String getPostTypeValue() {
        return postTypeValue;
    }

    public void setPostTypeValue(String postTypeValue) {
        this.postTypeValue = postTypeValue;
    }

    public String getPosterHandle() {
        return posterHandle;
    }

    public void setPosterHandle(String posterHandle) {
        this.posterHandle = posterHandle;
    }

    public ChirickViewModel getChirick() {
        return chirick;
    }

    public void setChirick(ChirickViewModel chirick) {
        this.chirick = chirick;
    }
}