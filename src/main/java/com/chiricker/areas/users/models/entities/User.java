package com.chiricker.areas.users.models.entities;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(name = "handle", nullable = false, unique = true)
    private String handle;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> authorities;

    @Column(name = "is_account_non_expired", nullable = false)
    private boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked", nullable = false)
    private boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired", nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "registered_on", nullable = false)
    private Date registeredOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = { CascadeType.ALL })
    private Set<Chirick> chiricks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = { CascadeType.ALL })
    private Set<Chirick> comments;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "rechiricks", cascade = { CascadeType.ALL })
    private Set<Chirick> rechiricks;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "likes", cascade = { CascadeType.ALL })
    private Set<Chirick> likes;

    @ManyToMany(mappedBy = "following", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<User> followers;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "user_relations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id"))
    private Set<User> following;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "timeline_id")
    private Timeline timeline;

    public User() {
    }

    @PrePersist
    public void onCreate() {
        this.profile = new Profile();
        this.registeredOn = new Date();
        this.timeline = new Timeline();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.isAccountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.isAccountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.isCredentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
    }

    public Set<Chirick> getChiricks() {
        return chiricks;
    }

    public void setChiricks(Set<Chirick> chiricks) {
        this.chiricks = chiricks;
    }

    public Set<Chirick> getComments() {
        return comments;
    }

    public void setComments(Set<Chirick> comments) {
        this.comments = comments;
    }

    public Set<Chirick> getRechiricks() {
        return rechiricks;
    }

    public void setRechiricks(Set<Chirick> rechiricks) {
        this.rechiricks = rechiricks;
    }

    public Set<Chirick> getLikes() {
        return likes;
    }

    public void setLikes(Set<Chirick> likes) {
        this.likes = likes;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}