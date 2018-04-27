package com.chiricker.services.notification;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.models.entities.Notification;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.repositories.NotificationRepository;
import com.chiricker.areas.users.services.notification.NotificationServiceImpl;
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

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServicNotifyUsersTests {

    private static final String VALID_HANDLE = "cyecize";
    private static final String VALID_HANDLE2 = "krasois";

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Chirick testChirick;
    private User testUser;

    @Before
    public void setup() {
        this.testUser = new User();
        this.testUser.setId("h4545hh545hy5heey5h");
        this.testUser.setHandle(VALID_HANDLE);

        User testUser2 = new User();
        testUser2.setId("h5h5h5w5e4ys6");
        testUser2.setHandle(VALID_HANDLE2);

        this.testChirick = new Chirick();
        this.testChirick.setId("d5r7e5gh345h3");
        this.testChirick.setChirick("wf4gw4fgf @cyecize gw4 @asd wg4g4wg4w");
        this.testChirick.setUser(testUser2);

        SimpleUserServiceModel simpleUser1 = new SimpleUserServiceModel();
        SimpleUserServiceModel simpleUser2 = new SimpleUserServiceModel();

        when(this.userService.getByHandleSimple(anyString())).thenReturn(null);
        when(this.userService.getByHandleSimple(VALID_HANDLE)).thenReturn(simpleUser1);
        when(this.userService.getByHandleSimple(VALID_HANDLE2)).thenReturn(simpleUser2);
        when(this.mapper.map(simpleUser1, User.class)).thenReturn(this.testUser);
        when(this.mapper.map(simpleUser2, User.class)).thenReturn(testUser2);
        when(this.notificationRepository.save(any())).thenAnswer(a -> a.getArgument(0));
    }

    @Test
    public void testNotifyUsers_WithNoHandles_ShouldReturnNull() {
        this.testChirick.setChirick("some chirick with no handles");
        Future result = this.notificationService.notifyUsers(this.testChirick);

        assertEquals("Result should be null.", null, result);
    }

    @Test
    public void testNotifyUsers_WithSelfReferencingHandle_ShouldReturnNull() throws ExecutionException, InterruptedException {
        this.testChirick.setChirick("some chirick @krasois with @krasois self referencing handles");
        Future result = this.notificationService.notifyUsers(this.testChirick);
        Set<Notification> notifications = (Set<Notification>) result.get();

        assertTrue("Result set should not have any entities.", notifications.size() == 0);
    }

    @Test
    public void testNotifyUsers_WithInvalidHandles_ShouldReturnNull() throws ExecutionException, InterruptedException {
        this.testChirick.setChirick("some chirick @asd with @asd no handles");
        Future result = this.notificationService.notifyUsers(this.testChirick);
        Set<Notification> notifications = (Set<Notification>) result.get();

        assertTrue("Result set should not have any entities.", notifications.size() == 0);
    }

    @Test
    public void testNotifyUsers_WithSelfReferencingAndValidHandle_ShouldHaveOneInSet() throws ExecutionException, InterruptedException {
        Future result = this.notificationService.notifyUsers(this.testChirick);
        Set<Notification> notifications = (Set<Notification>) result.get();

        assertTrue("Result set should have exactly 1 entity.", notifications.size() == 1);

        Notification n = notifications.stream().filter(x -> x.getChirick().getId().equals(this.testChirick.getId())).findFirst().orElse(null);

        assertNotEquals("Notification should not be null.", null, n);
        assertEquals("Notified user id is not correct.", this.testUser.getId(), n.getNotified().getId());
        assertEquals("Notification chirick id is not correct.", this.testChirick.getId(), n.getChirick().getId());
    }

    @Test
    public void testNotifyUsers_WithRepeatingValidHandles_ShouldHaveOneInSet() throws ExecutionException, InterruptedException {
        this.testChirick.setChirick("wf4gw4fgf @cyecize gw4 @cyecize wg4g4wg4w");
        Future result = this.notificationService.notifyUsers(this.testChirick);
        Set<Notification> notifications = (Set<Notification>) result.get();

        assertTrue("Result set should have exactly 1 entity.", notifications.size() == 1);

        Notification n = notifications.stream().filter(x -> x.getChirick().getId().equals(this.testChirick.getId())).findFirst().orElse(null);

        assertNotEquals("Notification should not be null.", null, n);
        assertEquals("Notified user id is not correct.", this.testUser.getId(), n.getNotified().getId());
        assertEquals("Notification chirick id is not correct.", this.testChirick.getId(), n.getChirick().getId());
    }
}