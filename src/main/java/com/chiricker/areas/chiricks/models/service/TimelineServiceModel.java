package com.chiricker.areas.chiricks.models.service;

import com.chiricker.areas.users.models.service.UserServiceModel;

import java.util.Set;

public class TimelineServiceModel {

    private String id;

    private UserServiceModel user;

    private Set<TimelinePostServiceModel> posts;

    public TimelineServiceModel() {
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

    public Set<TimelinePostServiceModel> getPosts() {
        return posts;
    }

    public void setPosts(Set<TimelinePostServiceModel> posts) {
        this.posts = posts;
    }
}