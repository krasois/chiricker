package com.chiricker.services.chirick;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.models.binding.CommentBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.view.ChirickCommentResultViewModel;
import com.chiricker.areas.chiricks.repositories.ChirickRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickServiceImpl;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.notification.NotificationService;
import com.chiricker.areas.users.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChirickServiceCommenTests {

    @Mock
    private ChirickRepository chirickRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ChirickServiceImpl chirickService;

    private CommentBindingModel testModel;
    private Chirick testChirick;
    private User testUser;

    @Before
    public void setup() {
        this.testUser = new User();
        this.testUser.setId("g54h54h3gh3g5");
        this.testUser.setHandle("pesho");

        this.testChirick = new Chirick();
        this.testChirick.setComments(new HashSet<>());
        this.testChirick.getComments().add(new Chirick());
        this.testChirick.getComments().add(new Chirick());
        this.testChirick.getComments().add(new Chirick());

        this.testModel = new CommentBindingModel();
        this.testModel.setComment("comment");
        this.testModel.setId("ewhnthh53h543h5");

        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(this.testUser);
        when(this.chirickRepository.findById(this.testModel.getId())).thenReturn(Optional.of(this.testChirick));
        when(this.chirickRepository.save(any())).thenAnswer(a -> a.getArgument(0));
    }

    @Test(expected = UserNotFoundException.class)
    public void testComment_WithInvalidHandle_ShouldThrowUserNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.chirickService.comment(this.testModel, "h5rwgwqwfawf");
    }

    @Test(expected = ChirickNotFoundException.class)
    public void testComment_WithInvalidChirickId_ShouldThrowChirickNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.testModel.setId("3334343135ggh");
        this.chirickService.comment(this.testModel, this.testUser.getHandle());
    }

    @Test
    public void testComment_WithValidData_ShouldReturnCorrectCommentSize() throws UserNotFoundException, ChirickNotFoundException {
        ChirickCommentResultViewModel model = this.chirickService.comment(this.testModel, this.testUser.getHandle());

        assertTrue("Comments size is not correct, is should be 4.", model.getCommentsSize() == 4);
    }

    @Test
    public void testComment_WithValidData_ShouldMapCorrectly() throws UserNotFoundException, ChirickNotFoundException {
        ChirickCommentResultViewModel model = this.chirickService.comment(this.testModel, this.testUser.getHandle());

        assertEquals("Comment Id is not mapped correctly.", model.getId(), this.testChirick.getId());
    }
}