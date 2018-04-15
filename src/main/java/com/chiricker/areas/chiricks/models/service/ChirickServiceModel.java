package com.chiricker.areas.chiricks.models.service;

import com.chiricker.areas.users.models.service.UserServiceModel;

import java.util.Date;
import java.util.Set;

public class ChirickServiceModel {

    private String id;

    private String chirick;

    private UserServiceModel user;

    private Date date;

    private Set<UserServiceModel> rechiricks;

    private Set<UserServiceModel> likes;

    private ChirickServiceModel parent;

    private Set<ChirickServiceModel> comments;

    public ChirickServiceModel() {
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

    public UserServiceModel getUser() {
        return user;
    }

    public void setUser(UserServiceModel user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<UserServiceModel> getRechiricks() {
        return rechiricks;
    }

    public void setRechiricks(Set<UserServiceModel> rechiricks) {
        this.rechiricks = rechiricks;
    }

    public Set<UserServiceModel> getLikes() {
        return likes;
    }

    public void setLikes(Set<UserServiceModel> likes) {
        this.likes = likes;
    }

    public ChirickServiceModel getParent() {
        return parent;
    }

    public void setParent(ChirickServiceModel parent) {
        this.parent = parent;
    }

    public Set<ChirickServiceModel> getComments() {
        return comments;
    }

    public void setComments(Set<ChirickServiceModel> comments) {
        this.comments = comments;
    }
}
