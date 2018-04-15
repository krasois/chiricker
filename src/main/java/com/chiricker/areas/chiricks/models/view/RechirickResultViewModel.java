package com.chiricker.areas.chiricks.models.view;

public class RechirickResultViewModel {

    private String id;

    private boolean isRechiricked;

    private long rechiricksSize;

    public RechirickResultViewModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRechiricked() {
        return isRechiricked;
    }

    public void setRechiricked(boolean rechiricked) {
        this.isRechiricked = rechiricked;
    }

    public long getRechiricksSize() {
        return rechiricksSize;
    }

    public void setRechiricksSize(long rechiricksSize) {
        this.rechiricksSize = rechiricksSize;
    }
}
