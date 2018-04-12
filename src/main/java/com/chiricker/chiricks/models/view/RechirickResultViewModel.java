package com.chiricker.chiricks.models.view;

public class RechirickResultViewModel {

    private boolean isRechiricked;

    private long rechiricksSize;

    public RechirickResultViewModel() {
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
