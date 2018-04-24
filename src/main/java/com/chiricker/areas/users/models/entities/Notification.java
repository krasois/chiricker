package com.chiricker.areas.users.models.entities;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @ManyToOne(optional = false)
    private User notified;

    @ManyToOne(optional = false)
    private Chirick chirick;

    @Column(name = "checked")
    private boolean checked;

    @Column(nullable = false, name = "date")
    private Date date;

    public Notification() {
    }

    @PrePersist
    public void onCreate() {
        date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getNotified() {
        return notified;
    }

    public void setNotified(User notified) {
        this.notified = notified;
    }

    public Chirick getChirick() {
        return chirick;
    }

    public void setChirick(Chirick chirick) {
        this.chirick = chirick;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
