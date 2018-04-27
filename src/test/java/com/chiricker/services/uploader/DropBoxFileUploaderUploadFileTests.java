package com.chiricker.services.uploader;

import com.chiricker.util.uploader.DropBoxFileUploader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DropBoxFileUploaderUploadFileTests {

    private static final String NEW_PIC_NAME = "g54gf4gf4f32f3232fggbsdrsgdrg";
    private static final String PIC_SHARING_PATH = "/" + NEW_PIC_NAME + ".png";
    private static final String CORRECT_SHARING_URL = "dl.dropboxusercontent.com/afjeawehg/chiricker/h54egwgawtegtea" + PIC_SHARING_PATH;

    @Mock
    private DbxClientV2 client;

    @InjectMocks
    private DropBoxFileUploader fileUploader;

    private MultipartFile testPicture;

    @Before
    public void setup() throws IOException, DbxException {
        this.testPicture = Mockito.mock(MultipartFile.class);

        SharedLinkMetadata slm = Mockito.mock(SharedLinkMetadata.class);
        DbxUserFilesRequests files = Mockito.mock(DbxUserFilesRequests.class);
        UploadBuilder uploadBuilder = Mockito.mock(UploadBuilder.class);
        DbxUserSharingRequests sharing = Mockito.mock(DbxUserSharingRequests.class);
        InputStream is = Mockito.mock(InputStream.class);

        when(slm.getUrl()).thenReturn("www.dropbox.com/afjeawehg/chiricker/h54egwgawtegtea" + PIC_SHARING_PATH + "?dl=0");
        when(this.testPicture.getOriginalFilename()).thenReturn("ogPicName.png");
        when(this.testPicture.getInputStream()).thenReturn(is);
        when(uploadBuilder.uploadAndFinish(any(InputStream.class))).thenReturn(null);
        when(files.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(this.client.files()).thenReturn(files);
        when(this.client.sharing()).thenReturn(sharing);
        when(sharing.createSharedLinkWithSettings(anyString(), any(SharedLinkSettings.class))).thenReturn(slm);
    }

    @Test
    public void testUpload_WithValidData_ShouldReturnProperSharingUrl() {
        String sharingUrl = this.fileUploader.uploadFile(this.testPicture);

        assertEquals("Sharing link is not correct.", sharingUrl, CORRECT_SHARING_URL);
    }
}