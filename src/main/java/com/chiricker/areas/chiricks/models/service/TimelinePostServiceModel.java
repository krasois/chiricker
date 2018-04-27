package com.chiricker.areas.chiricks.models.service;

import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.users.models.service.UserServiceModel;

import java.util.Date;

public class TimelinePostServiceModel {

    private String id;

    private UserServiceModelTP to;

    private UserServiceModelTP from;

    private ChirickServiceModel chirick;

    private Date date;

    private TimelinePostType postType;

    private TimelineServiceModel timeline;

    public TimelinePostServiceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserServiceModelTP getFrom() {
        return from;
    }

    public void setFrom(UserServiceModelTP from) {
        this.from = from;
    }

    public UserServiceModelTP getTo() {
        return to;
    }

    public void setTo(UserServiceModelTP to) {
        this.to = to;
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

    public TimelineServiceModel getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineServiceModel timeline) {
        this.timeline = timeline;
    }
}
