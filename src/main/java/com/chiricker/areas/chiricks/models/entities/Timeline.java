package com.chiricker.areas.chiricks.models.entities;

import com.chiricker.areas.users.models.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "timelines")
public class Timeline {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @OneToOne(mappedBy = "timeline", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<TimelinePost> posts;

    public Timeline() {
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

    public Set<TimelinePost> getPosts() {
        return posts;
    }

    public void setPosts(Set<TimelinePost> posts) {
        this.posts = posts;
    }
}