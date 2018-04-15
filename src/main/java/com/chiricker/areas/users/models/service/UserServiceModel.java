package com.chiricker.areas.users.models.service;

import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineServiceModel;

import java.util.Date;
import java.util.Set;

public class UserServiceModel {

    private String id;

    private String handle;

    private String name;

    private String password;

    private String email;

    private Set<RoleServiceModel> authorities;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    private Date registeredOn;

    private Set<ChirickServiceModel> chiricks;

    private Set<ChirickServiceModel> comments;

    private Set<ChirickServiceModel> rechiricks;

    private Set<ChirickServiceModel> likes;

    private Set<UserServiceModel> followers;

    private Set<UserServiceModel> following;

    private ProfileServiceModel profile;

    private TimelineServiceModel timeline;

    public UserServiceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleServiceModel> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<RoleServiceModel> authorities) {
        this.authorities = authorities;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
    }

    public Set<ChirickServiceModel> getChiricks() {
        return chiricks;
    }

    public void setChiricks(Set<ChirickServiceModel> chiricks) {
        this.chiricks = chiricks;
    }

    public Set<ChirickServiceModel> getComments() {
        return comments;
    }

    public void setComments(Set<ChirickServiceModel> comments) {
        this.comments = comments;
    }

    public Set<ChirickServiceModel> getRechiricks() {
        return rechiricks;
    }

    public void setRechiricks(Set<ChirickServiceModel> rechiricks) {
        this.rechiricks = rechiricks;
    }

    public Set<ChirickServiceModel> getLikes() {
        return likes;
    }

    public void setLikes(Set<ChirickServiceModel> likes) {
        this.likes = likes;
    }

    public Set<UserServiceModel> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserServiceModel> followers) {
        this.followers = followers;
    }

    public Set<UserServiceModel> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserServiceModel> following) {
        this.following = following;
    }

    public ProfileServiceModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileServiceModel profile) {
        this.profile = profile;
    }

    public TimelineServiceModel getTimeline() {
        return timeline;
    }

    public void setTimeline(TimelineServiceModel timeline) {
        this.timeline = timeline;
    }
}