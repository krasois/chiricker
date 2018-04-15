package com.chiricker.areas.chiricks.models.view;

import java.util.Date;

public class ChirickDetailsViewModel {

    private ChirickUserViewModel user;

    private ChirickViewModel chirick;

    private Date date;

    public ChirickDetailsViewModel() {
    }

    public ChirickUserViewModel getUser() {
        return user;
    }

    public void setUser(ChirickUserViewModel user) {
        this.user = user;
    }

    public ChirickViewModel getChirick() {
        return chirick;
    }

    public void setChirick(ChirickViewModel chirick) {
        this.chirick = chirick;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
