package com.chiricker.areas.users.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Future;

public interface FileUploader {

    Future uploadFile(String userHandle, MultipartFile profilePicture);
}
