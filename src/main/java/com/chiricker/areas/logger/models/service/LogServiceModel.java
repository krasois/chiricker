package com.chiricker.areas.logger.models.service;

import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.users.models.service.UserServiceModel;

import java.util.Date;

public class LogServiceModel {

    private String id;

    private UserServiceModel user;

    private Operation operation;

    private String modifiedTable;

    private Date date;

    public LogServiceModel() {
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

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getModifiedTable() {
        return modifiedTable;
    }

    public void setModifiedTable(String modifiedTable) {
        this.modifiedTable = modifiedTable;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
