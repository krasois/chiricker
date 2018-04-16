package com.chiricker.areas.admin.models.view;

import com.chiricker.areas.logger.models.entities.enums.Operation;

import java.util.Date;

public class LogViewModel {

    private String userId;

    private Operation operation;

    private String modifiedTable;

    private Date date;

    public LogViewModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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