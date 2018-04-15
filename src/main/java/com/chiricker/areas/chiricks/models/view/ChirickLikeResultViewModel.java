package com.chiricker.areas.chiricks.models.view;

public class ChirickLikeResultViewModel {

    private String id;

    private boolean isLiked;

    private long likesSize;

    public ChirickLikeResultViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        this.isLiked = liked;
    }

    public long getLikesSize() {
        return likesSize;
    }

    public void setLikesSize(long likesSize) {
        this.likesSize = likesSize;
    }
}