package com.chiricker.services.uploader;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.util.uploader.DropBoxFileUploader;
import com.chiricker.util.uploader.FileManager;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FileManagerUpdateProfilePictureTests {

    @Mock
    private UserService userService;
    @Mock
    private DropBoxFileUploader fileUploader;

    @InjectMocks
    private FileManager fileManager;

    private static final String DEFAULT_RESULT = "NONE";

    private MultipartFile testPicture;
    private String testUserHandle;
    private String testCurrentProfilePicUrl;
    private String newProfilePic;

    @Before
    public void setup() {
        this.testPicture = Mockito.mock(MultipartFile.class);
        this.testUserHandle = "pesho";
        this.testCurrentProfilePicUrl = "dl.dropbox.dl.dropboxusercontent.com/afjeawehg/chiricker/h54egwgawtegtea/g54gf4gf4f32f3232fggbsdrsgdrg.jpg";
        this.newProfilePic = "dl.dropbox.dl.dropboxusercontent.com/afjeawehg/chiricker/h54egwgawtegtea/346365634.jpg";

        when(this.fileUploader.uploadFile(this.testPicture)).thenReturn(this.newProfilePic);
    }

    @Test
    public void testUpdatePictur_WithValidFile_ShouldReturnNewPictureLink() throws UserNotFoundException, ExecutionException, InterruptedException {
        when(this.testPicture.getSize()).thenReturn(2L);
        Future future = this.fileManager.updateProfilePicture(this.testUserHandle, this.testCurrentProfilePicUrl, this.testPicture);
        String picUrl = (String) future.get();

        assertEquals("Result picture URL is not correct.", this.newProfilePic, picUrl);
    }

    @Test
    public void testUpdatePictur_WithInvalidFile_ShouldReturnDefaultResult() throws UserNotFoundException, ExecutionException, InterruptedException {
        when(this.testPicture.getSize()).thenReturn(0L);
        Future future = this.fileManager.updateProfilePicture(this.testUserHandle, this.testCurrentProfilePicUrl, this.testPicture);
        String picUrl = (String) future.get();

        assertEquals("Result picture URL is not correct.", DEFAULT_RESULT, picUrl);
    }
}
