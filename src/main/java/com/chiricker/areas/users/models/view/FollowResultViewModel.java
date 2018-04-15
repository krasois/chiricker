package com.chiricker.areas.users.models.view;

public class FollowResultViewModel {

    private boolean unfollowed;

    public FollowResultViewModel() {
    }

    public boolean isUnfollowed() {
        return unfollowed;
    }

    public void setUnfollowed(boolean unfollowed) {
        this.unfollowed = unfollowed;
    }
}
