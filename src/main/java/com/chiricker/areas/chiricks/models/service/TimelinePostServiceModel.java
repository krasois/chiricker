package com.chiricker.areas.chiricks.models.service;

import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.users.models.service.UserServiceModel;

import java.util.Date;

public class TimelinePostServiceModel {

    private String id;

    private UserServiceModel user;

    private ChirickServiceModel chirick;

    private Date date;

    private TimelinePostType postType;

    public TimelinePostServiceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserServiceModel getUser() {
        return user;
    }

    public void setUser(UserServiceModel user) {
        this.user = user;
    }

    public ChirickServiceModel getChirick() {
        return chirick;
    }

    public void setChirick(ChirickServiceModel chirick) {
        this.chirick = chirick;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TimelinePostType getPostType() {
        return postType;
    }

    public void setPostType(TimelinePostType postType) {
        this.postType = postType;
    }
}
