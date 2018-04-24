package com.chiricker.util.uploader;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.services.user.UserService;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Future;

@Component
public class FileUploaderImpl implements FileUploader {

    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://dl.dropboxusercontent.com/s/y7i72l1yblmfydj/defaultProfileImage.png";

    private static final String DEFAULT_RESULT = "NONE";
    private static final String DROPBOX_DOMAIN = "www.dropbox.com";
    private static final String DROPBOX_SHARING_DOMAIN = "dl.dropboxusercontent.com";

    private static final int MIN_FILE_SIZE = 0;

    private final UserService userService;
    private final DbxClientV2 client;

    @Autowired
    public FileUploaderImpl(UserService userService, DbxClientV2 client) {
        this.userService = userService;
        this.client = client;
    }

    @Async
    public Future uploadFile(String userHandle, MultipartFile profilePicture) throws UserNotFoundException {
        String result = DEFAULT_RESULT;

        User user = this.userService.getByHandle(userHandle);
        if (user == null) throw new UserNotFoundException();

        String currentProfilePicUrl = user.getProfile().getProfilePicUrl();
        if (profilePicture.getSize() > MIN_FILE_SIZE) {

            try (InputStream in = profilePicture.getInputStream()) {

                String fileName = profilePicture.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileRandomName = "/" + UUID.randomUUID().toString() + fileExtension;

                FileMetadata fileMetadata = this.client.files()
                        .uploadBuilder(newFileRandomName)
                        .uploadAndFinish(in);

                SharedLinkMetadata slm = this.client.sharing()
                        .createSharedLinkWithSettings(newFileRandomName,
                                SharedLinkSettings.newBuilder().withRequestedVisibility(RequestedVisibility.PUBLIC).build());

                result = slm.getUrl()
                        .replace(DROPBOX_DOMAIN, DROPBOX_SHARING_DOMAIN)
                        .replace("?dl=0", "");

                userService.updateUserProfilePicUrl(userHandle, result);

            } catch (IOException | DbxException e) {
                System.out.println(e.getMessage());
            }

            if (!currentProfilePicUrl.equals(DEFAULT_PROFILE_PICTURE_URL)) {
                String filePath = currentProfilePicUrl.substring(currentProfilePicUrl.lastIndexOf("/"));

                try {
                    this.client.files().deleteV2(filePath);
                } catch (DbxException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return new AsyncResult<>(result);
    }
}