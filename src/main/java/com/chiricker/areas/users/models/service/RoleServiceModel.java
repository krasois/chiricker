package com.chiricker.areas.users.models.service;

public class RoleServiceModel {

    private Long id;

    private String authority;

    public RoleServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}