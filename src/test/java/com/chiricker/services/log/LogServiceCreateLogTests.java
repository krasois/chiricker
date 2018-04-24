package com.chiricker.services.log;

import com.chiricker.areas.logger.models.entities.Log;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.areas.logger.models.service.LogServiceModel;
import com.chiricker.areas.logger.repositories.LogRepository;
import com.chiricker.areas.logger.services.log.LogServiceImpl;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LogServiceCreateLogTests {

    @Mock
    private LogRepository logRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private LogServiceImpl logService;

    private User user;

    @Before
    public void setup() {
        this.user = new User();
        this.user.setId("asdsadadasdasd");

        when(this.userService.getByHandle(user.getId())).thenReturn(user);
        when(this.mapper.map(any(Log.class), eq(LogServiceModel.class))).thenAnswer(a -> {
            Log l = a.getArgument(0);
            LogServiceModel m = new LogServiceModel();
            m.setDate(new Date());
            m.setId(l.getId());
            m.setModifiedTable(l.getModifiedTable());
            if (l.getUserId() != null) {
                m.setUser(new UserServiceModel());
            }
            m.setOperation(l.getOperation());
            return m;
        });
    }

    @Test
    public void testCreateLog_WithUserHandleNull_ShouldCreateLog() {
        LogServiceModel log = this.logService.createLog(null, Operation.REGISTER, "users");

        assertEquals("User should be null.", log.getUser(), null);
        assertEquals("Operation should be REGISTER.", log.getOperation(), Operation.REGISTER);
        assertEquals("Modified table should be 'users'.", log.getModifiedTable(), "users");
    }

    @Test
    public void testCreateLog_WithValidHandle_ShouldCreateLog() {
        LogServiceModel log = this.logService.createLog(this.user.getId(), Operation.RECHIRICK, "chiricks");

        assertNotEquals("User should not be null.", log.getUser(), null);
        assertEquals("Operation should be RECHIRICK.", log.getOperation(), Operation.RECHIRICK);
        assertEquals("Modified table should be 'chiricks'.", log.getModifiedTable(), "chiricks");
    }
}