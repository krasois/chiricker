package com.chiricker.chiricks.models.view;

public class ChirickLikeResultViewModel {

    private boolean isLiked;

    private long likesSize;

    public ChirickLikeResultViewModel() {
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