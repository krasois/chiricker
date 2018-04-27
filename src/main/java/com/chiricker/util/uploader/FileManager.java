package com.chiricker.util.uploader;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import com.dropbox.core.v2.files.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class FileManager {

    private static final int MIN_FILE_SIZE = 0;
    private static final String DEFAULT_RESULT = "NONE";
    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://dl.dropboxusercontent.com/s/y7i72l1yblmfydj/defaultProfileImage.png";

    private final UserService userService;
    private final DropBoxFileUploader fileUploader;

    @Autowired
    public FileManager(UserService userService, DropBoxFileUploader fileUploader) {
        this.userService = userService;
        this.fileUploader = fileUploader;
    }

    @Async
    public Future updateProfilePicture(String userHandle, String currentProfilePicUrl, MultipartFile profilePicture) throws UserNotFoundException {
        String result = DEFAULT_RESULT;

        if (profilePicture.getSize() > MIN_FILE_SIZE) {
            result = this.fileUploader.uploadFile(profilePicture);

            if (!result.equals(DEFAULT_RESULT)) {
                this.userService.updateUserProfilePicUrl(userHandle, result);

                if (!currentProfilePicUrl.equals(DEFAULT_PROFILE_PICTURE_URL)) {
                    String filePath = currentProfilePicUrl.substring(currentProfilePicUrl.lastIndexOf("/"));
                    this.fileUploader.deleteFile(filePath);
                }
            }
        }

        return new AsyncResult<>(result);
    }
}