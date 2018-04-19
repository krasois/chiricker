package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.ProfileServiceModel;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.repositories.UserRepository;
import com.chiricker.areas.users.services.role.RoleService;
import com.chiricker.areas.users.services.user.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceEnableOrDisableUserByIdTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @Before
    public void setup() {
        this.testUser = new User();
        this.testUser.setId("rwr4yh45-3gq553-y35k753t2-h5312t3h");
        this.testUser.setHandle("pesho");

        when(this.userRepository.findById(this.testUser.getId())).thenReturn(Optional.of(this.testUser));
        when(this.userRepository.save(any(User.class))).thenAnswer(a -> a.getArgument(0));
        when(mapper.map(any(User.class), eq(UserServiceModel.class))).thenAnswer(r ->  {
            User model = r.getArgument(0);
            UserServiceModel userModel = new UserServiceModel();

            userModel.setId(model.getId());
            userModel.setHandle(model.getHandle());
            userModel.setEnabled(model.isEnabled());

            return userModel;
        });
    }

    @Test
    public void testEnableOrDisable_WithValidId_ShouldNotThrowOrReturnNull() throws UserNotFoundException {
        UserServiceModel model = this.userService.enableUser(this.testUser.getId());

        assertNotEquals("Result model should not be null.", model, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testEnableOrDisable_WithInvalidId_ShouldThrow() throws UserNotFoundException {
        this.userService.disableUser("ddsadsdas");
    }

    @Test
    public void testEnable_WithValidId_ShouldEnableUser() throws UserNotFoundException {
        this.testUser.setEnabled(false);
        UserServiceModel model = this.userService.enableUser(this.testUser.getId());

        assertTrue("User account did not enable.", model.isEnabled());
    }

    @Test
    public void testDisable_WithValidId_ShouldDisableUser() throws UserNotFoundException {
        this.testUser.setEnabled(true);
        UserServiceModel model = this.userService.disableUser(this.testUser.getId());

        assertFalse("User account did not disable.", model.isEnabled());
    }
}