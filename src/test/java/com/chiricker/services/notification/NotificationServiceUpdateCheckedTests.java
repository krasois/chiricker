package com.chiricker.services.notification;

import com.chiricker.areas.users.models.entities.Notification;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceUpdateCheckedTests {

    private static final String N1_ID = "r4hh5h54hgh";
    private static final String N2_ID = "w664w4e5";

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private List<NotificationViewModel> testModels;

    @Before
    public void setup() {
        Notification testN1 = new Notification();
        testN1.setId(N1_ID);

        Notification testN2 = new Notification();
        testN2.setId(N2_ID);

        this.testModels = new ArrayList<>();

        when(this.notificationRepository.findById(anyString())).thenReturn(Optional.empty());
        when(this.notificationRepository.findById(eq(N1_ID))).thenReturn(Optional.of(testN1));
        when(this.notificationRepository.findById(eq(N2_ID))).thenReturn(Optional.of(testN2));
        when(this.notificationRepository.saveAndFlush(any())).thenAnswer(a -> a.getArgument(0));
    }

    @Test
    public void testUpdateChecked_WithEmptyModelsList_ShouldReturnEmptyList() throws ExecutionException, InterruptedException {
        Future future = this.notificationService.updateChecked(new ArrayList<>());
        List<Notification> result = (List<Notification>) future.get();

        assertTrue("Result list should be empty.", result.size() == 0);
    }

    @Test
    public void testUpdateChecked_WithInvalidModels_ShouldReturnEmptyList() throws ExecutionException, InterruptedException {
        this.testModels.add(new NotificationViewModel() {{ setId("cvbcvb"); }});
        this.testModels.add(new NotificationViewModel() {{ setId("345345"); }});
        Future future = this.notificationService.updateChecked(this.testModels);
        List<Notification> result = (List<Notification>) future.get();

        assertTrue("Result list should be empty.", result.size() == 0);
    }

    @Test
    public void testUpdateChecked_WithValidModels_ShouldReturnCorrectList() throws ExecutionException, InterruptedException {
        this.testModels.add(new NotificationViewModel() {{ setId(N1_ID); }});
        this.testModels.add(new NotificationViewModel() {{ setId(N2_ID); }});
        Future future = this.notificationService.updateChecked(this.testModels);
        List<Notification> result = (List<Notification>) future.get();

        Notification n1 = result.get(0);
        Notification n2 = result.get(1);

        assertTrue("Result list should have 2 entities.", result.size() == 2);
        assertTrue("Notification 1 should have checked as true.", n1.isChecked());
        assertTrue("Notification 2 should have checked as true.", n2.isChecked());
    }

    @Test
    public void testUpdateChecked_WithOneValidAndOneInvalidModel_ShouldReturnCorrectList() throws ExecutionException, InterruptedException {
        this.testModels.add(new NotificationViewModel() {{ setId("cvbcvb"); }});
        this.testModels.add(new NotificationViewModel() {{ setId(N1_ID); }});
        Future future = this.notificationService.updateChecked(this.testModels);
        List<Notification> result = (List<Notification>) future.get();

        Notification n = result.get(0);

        assertTrue("Result list should have 1 entity.", result.size() == 1);
        assertTrue("Notification should have checked as true.", n.isChecked());
    }
}