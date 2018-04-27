package com.chiricker.areas.chiricks.models.entities.enums;

public enum TimelinePostType {

    CHIRICK(""),
    RECHIRICK("rechiricked"),
    LIKE("liked"),
    COMMENT("commented");

    private final String value;

    TimelinePostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}