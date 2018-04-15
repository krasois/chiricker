package com.chiricker.areas.users.utils;

import com.chiricker.areas.users.services.user.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Future;

public interface FileUploader {

    Future uploadFile(UserService userService, String userHandle, String currentProfilePicUrl, MultipartFile profilePicture);
}
