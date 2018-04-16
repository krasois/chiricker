package com.chiricker.areas.logger.models.entities;

import com.chiricker.areas.logger.models.entities.enums.Operation;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(nullable = false, unique = true, updatable = false)
    private String id;

    @Column
    private String userId;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column(nullable = false, updatable = false)
    private String modifiedTable;

    @Column(nullable = false, updatable = false)
    private Date date;

    public Log() {
    }

    @PrePersist
    public void onCreate() {
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
