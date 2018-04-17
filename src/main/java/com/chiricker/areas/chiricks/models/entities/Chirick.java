package com.chiricker.areas.chiricks.models.entities;

import com.chiricker.areas.users.models.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "chiricks")
public class Chirick {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(unique = true, nullable = false)
    private String id;

    @Column(name = "chirick", nullable = false)
    private String chirick;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = User.class)
    private User user;

    @Column(name = "date", nullable = false, updatable = false)
    private Date date;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinTable(name = "users_rechiricks",
            joinColumns = @JoinColumn(name = "chirick_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> rechiricks;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "users_likes",
            joinColumns = @JoinColumn(name = "chirick_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> likes;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "parent_id")
    private Chirick parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, mappedBy = "parent")
    private Set<Chirick> comments;

    public Chirick() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.date == null) this.date = new Date();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<User> getRechiricks() {
        return rechiricks;
    }

    public void setRechiricks(Set<User> rechiricks) {
        this.rechiricks = rechiricks;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Chirick getParent() {
        return parent;
    }

    public void setParent(Chirick parent) {
        this.parent = parent;
    }

    public Set<Chirick> getComments() {
        return comments;
    }

    public void setComments(Set<Chirick> comments) {
        this.comments = comments;
    }
}
