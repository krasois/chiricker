package com.chiricker.util.uploader;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Future;

public interface FileUploader {

    Future uploadFile(String userHandle, MultipartFile profilePicture) throws UserNotFoundException;
}
