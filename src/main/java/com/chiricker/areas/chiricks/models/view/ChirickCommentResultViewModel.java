package com.chiricker.areas.chiricks.models.view;

public class ChirickCommentResultViewModel {

    private String id;

    private long commentsSize;

    public ChirickCommentResultViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCommentsSize() {
        return commentsSize;
    }

    public void setCommentsSize(long commentsSize) {
        this.commentsSize = commentsSize;
    }
}
