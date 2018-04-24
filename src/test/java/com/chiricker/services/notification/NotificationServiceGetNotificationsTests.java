package com.chiricker.services.notification;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.models.entities.Notification;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.NotificationViewModel;
import com.chiricker.areas.users.repositories.NotificationRepository;
import com.chiricker.areas.users.services.notification.NotificationServiceImpl;
import com.chiricker.areas.users.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceGetNotificationsTests {

    private static final String HANDLE = "pesho";
    private static final String N1_ID = "g5egw4wstgt4";
    private static final String N2_ID = "eggwegwegw";
    private static final String N1_CHIRICK = "awdawdawawrvgr";
    private static final String N2_CHIRICK = "j57tj @krasois 75j5j6hjh64";
    private static final String N1_C_USER_HANDLE = "gosho";
    private static final String N1_C_USER_NAME = "Gosho";
    private static final String N2_C_USER_HANDLE = "tosho";
    private static final String N2_C_USER_NAME = "Tosho";

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Before
    public void setup() {
        List<Notification> notifications = new ArrayList<>();
        Notification n1 = new Notification();
        n1.setId(N1_ID);
        n1.setChirick(new Chirick() {{
            setChirick(N1_CHIRICK);
            setUser(new User() {{
                setHandle(N1_C_USER_HANDLE);
                setName(N1_C_USER_NAME);
            }});
        }});
        n1.setNotified(new User());
        notifications.add(n1);

        Notification n2 = new Notification();
        n2.setId(N2_ID);
        n2.setChirick(new Chirick() {{
            setChirick(N2_CHIRICK);
            setUser(new User() {{
                setHandle(N2_C_USER_HANDLE);
                setName(N2_C_USER_NAME);
            }});
        }});
        n2.setNotified(new User());
        notifications.add(n2);

        when(this.notificationRepository.findAllByCheckedFalseAndNotifiedHandleOrderByDateDesc(anyString())).thenReturn(new ArrayList<>());
        when(this.notificationRepository.findAllByCheckedFalseAndNotifiedHandleOrderByDateDesc(eq(HANDLE))).thenReturn(notifications);
    }

    @Test
    public void testGetNotification_WithInvalidHandle_ShouldReturnEmptyList() {
        List<NotificationViewModel> wfwffa = this.notificationService.getNotifications("wfwffa");

        assertTrue("Result list has more or less than 0 models.", wfwffa.size() == 0);
    }

    @Test
    public void testGetNotifications_WithValidHandle_ShouldMapCorrectly() {
        List<NotificationViewModel> notifications = this.notificationService.getNotifications(HANDLE);
        NotificationViewModel n1 = notifications.stream().filter(n -> n.getId().equals(N1_ID)).findFirst().orElse(null);
        NotificationViewModel n2 = notifications.stream().filter(n -> n.getId().equals(N2_ID)).findFirst().orElse(null);

        String expectedN2Chirick = "j57tj <a class=\"text-info\" href=\"@krasois\">@krasois</a> 75j5j6hjh64";

        assertNotEquals("Notification 1 should not be null.", null, n1);
        assertNotEquals("Notification 2 should not be null.", null, n2);

        assertEquals("Notification 1 id is not mapped correctly.", N1_ID, n1.getId());
        assertEquals("Notification 2 id is not mapped correctly.", N2_ID, n2.getId());

        assertEquals("Notification 1 chirick is not mapped correctly.", N1_CHIRICK, n1.getChirickContent());
        assertEquals("Notification 2 chirick is not mapped correctly.", expectedN2Chirick, n2.getChirickContent());

        assertEquals("Notification 1 chirick user handle is not mapped correctly.", N1_C_USER_HANDLE, n1.getUserHandle());
        assertEquals("Notification 2 chirick user handle is not mapped correctly.", N2_C_USER_HANDLE, n2.getUserHandle());

        assertEquals("Notification 1 chirick user name is not mapped correctly.", N1_C_USER_NAME, n1.getUserName());
        assertEquals("Notification 2 chirick user name is not mapped correctly.", N2_C_USER_NAME, n2.getUserName());
    }
}
