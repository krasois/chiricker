package com.chiricker.services.uploader;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.ProfileServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.util.uploader.FileUploaderImpl;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FileUploaderUploadFileTests {

    private static final String DEFAUL_RESPONSE = "NONE";
    private static final String NEW_PIC_NAME = "g54gf4gf4f32f3232fggbsdrsgdrg";
    private static final String PIC_SHARING_PATH = "/" + NEW_PIC_NAME + ".png";
    private static final String CORRECT_SHARING_URL = "dl.dropboxusercontent.com/afjeawehg/chiricked/h54egwgawtegtea" + PIC_SHARING_PATH;

    @Mock
    private UserService userService;
    @Mock
    private DbxClientV2 client;

    @InjectMocks
    private FileUploaderImpl fileUploader;

    private User testUser;
    private MultipartFile testPicture;

    @Before
    public void setup() throws IOException, DbxException {
        this.testPicture = Mockito.mock(MultipartFile.class);

        this.testUser = new User();
        this.testUser.setHandle("pesho");
        this.testUser.setProfile(new Profile());
        this.testUser.getProfile().setProfilePicUrl("dropbox.com/5h3hhherhr/chiricker/asdasw3eh5h5/asdadawdawd.png");

        SharedLinkMetadata slm = Mockito.mock(SharedLinkMetadata.class);
        DbxUserFilesRequests files = Mockito.mock(DbxUserFilesRequests.class);
        UploadBuilder uploadBuilder = Mockito.mock(UploadBuilder.class);
        DbxUserSharingRequests sharing = Mockito.mock(DbxUserSharingRequests.class);
        InputStream is = Mockito.mock(InputStream.class);

        when(slm.getUrl()).thenReturn("www.dropbox.com/afjeawehg/chiricked/h54egwgawtegtea" + PIC_SHARING_PATH + "?dl=0");
        when(this.testPicture.getOriginalFilename()).thenReturn("ogPicName.png");
        when(this.testPicture.getInputStream()).thenReturn(is);
        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(this.testUser);
        when(uploadBuilder.uploadAndFinish(any(InputStream.class))).thenReturn(null);
        when(files.uploadBuilder(anyString())).thenReturn(uploadBuilder);
        when(this.client.files()).thenReturn(files);
        when(this.client.sharing()).thenReturn(sharing);
        when(sharing.createSharedLinkWithSettings(anyString(), any(SharedLinkSettings.class))).thenReturn(slm);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpload_WithInvalidUserHandle_ShouldThrow() throws UserNotFoundException {
        this.fileUploader.uploadFile("vvvv", this.testPicture);
    }

    @Test
    public void testUpload_WithNoMultipartFile_ShouldReturnDefaultResponse() throws UserNotFoundException, ExecutionException, InterruptedException {
        when(this.testPicture.getSize()).thenReturn(0L);
        Future future = this.fileUploader.uploadFile(this.testUser.getHandle(), this.testPicture);
        String result = (String) future.get();

        assertEquals("Result string should equal default response.", result, DEFAUL_RESPONSE);
    }

    @Test
    public void testUpload_WithValidData_ShouldReturnProperSharingUrl() throws UserNotFoundException, ExecutionException, InterruptedException {
        when(this.testPicture.getSize()).thenReturn(2L);
        Future future = this.fileUploader.uploadFile(this.testUser.getHandle(), this.testPicture);
        String sharingUrl = (String) future.get();

        assertEquals("Sharing link is not correct.", sharingUrl, CORRECT_SHARING_URL);
    }
}