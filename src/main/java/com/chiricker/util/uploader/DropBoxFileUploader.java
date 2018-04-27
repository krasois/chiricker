package com.chiricker.util.uploader;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Future;

public class DropBoxFileUploader {

    private static final String DROPBOX_DOMAIN = "www.dropbox.com";
    private static final String DROPBOX_SHARING_DOMAIN = "dl.dropboxusercontent.com";

    private final DbxClientV2 client;

    public DropBoxFileUploader(DbxClientV2 client) {
        this.client = client;
    }

    @Async
    public Future deleteFile(String path) {
        try {
            DeleteResult deleteResult = this.client.files().deleteV2(path);
            return new AsyncResult<>(deleteResult);
        } catch (DbxException e) {
            return null;
        }
    }

    public String uploadFile(MultipartFile profilePicture) {
        String result;
        try (InputStream in = profilePicture.getInputStream()) {

            String fileName = profilePicture.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String newFileRandomName = "/" + UUID.randomUUID().toString() + fileExtension;

            this.client.files()
                    .uploadBuilder(newFileRandomName)
                    .uploadAndFinish(in);

            SharedLinkMetadata slm = this.client.sharing()
                    .createSharedLinkWithSettings(newFileRandomName,
                            SharedLinkSettings.newBuilder().withRequestedVisibility(RequestedVisibility.PUBLIC).build());

            result = slm.getUrl()
                    .replace(DROPBOX_DOMAIN, DROPBOX_SHARING_DOMAIN)
                    .replace("?dl=0", "");

        } catch (IOException | DbxException e) {
            return null;
        }

        return result;
    }
}