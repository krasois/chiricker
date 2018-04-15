package com.chiricker.areas.chiricks.models.view;

public class ChirickViewModel {

    private String id;

    private String chirick;

    private long rechiricksSize;

    private boolean isRechiricked;

    private long likesSize;

    private boolean isLiked;

    private long commentsSize;

    private boolean isCommented;

    private String userHandle;

    private String userName;

    private String userProfilePicUrl;

    private String parentUrl;

    public ChirickViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChirick() {
        return chirick;
    }

    public void setChirick(String chirick) {
        this.chirick = chirick;
    }

    public long getRechiricksSize() {
        return rechiricksSize;
    }

    public void setRechiricksSize(long rechiricksSize) {
        this.rechiricksSize = rechiricksSize;
    }

    public boolean isRechiricked() {
        return isRechiricked;
    }

    public void setRechiricked(boolean rechiricked) {
        isRechiricked = rechiricked;
    }

    public long getLikesSize() {
        return likesSize;
    }

    public void setLikesSize(long likesSize) {
        this.likesSize = likesSize;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public long getCommentsSize() {
        return commentsSize;
    }

    public void setCommentsSize(long commentsSize) {
        this.commentsSize = commentsSize;
    }

    public boolean isCommented() {
        return isCommented;
    }

    public void setCommented(boolean commented) {
        isCommented = commented;
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

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }
}
