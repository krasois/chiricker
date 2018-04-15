package com.chiricker.areas.chiricks.models.entities;

import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.users.models.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "timeline_posts")
public class TimelinePost {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chirick chirick;

    @Column(nullable = false, updatable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private TimelinePostType postType;

    public TimelinePost() {
    }

    @PrePersist
    public void onCreate() {
        this.setDate(new Date());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chirick getChirick() {
        return chirick;
    }

    public void setChirick(Chirick chirick) {
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