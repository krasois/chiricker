package com.chiricker.users.validation;

public enum MIMEType {
    PNG("image/png"), JPEG("image/jpeg"), BMP("image/bmp");

    private final String mimeType;

    MIMEType(String type) {
        mimeType = type;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}