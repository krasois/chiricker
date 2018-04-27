package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.ProfileServiceModel;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceUpdateUserProfilePicUrlTests {

    private static final String NEW_PIC_URL = "www.pics.com/findAllInSet";

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

    private User user;

    @Before
    public void setup() {
        this.user = new User();
        this.user.setHandle("pesho");
        this.user.setProfile(new Profile());

        when(this.userRepository.save(any())).thenAnswer(a -> a.getArgument(0));
        when(this.userRepository.findByHandle(this.user.getHandle())).thenReturn(this.user);
        when(this.mapper.map(any(User.class), eq(UserServiceModel.class))).thenAnswer(r ->  {
            User model = r.getArgument(0);
            UserServiceModel userModel = new UserServiceModel();

            userModel.setHandle(model.getHandle());
            userModel.setProfile(new ProfileServiceModel());
            userModel.getProfile().setProfilePicUrl(model.getProfile().getProfilePicUrl());
            return userModel;
        });
    }

    @Test
    public void testUpdateProfilePic_WithValidHandle_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        Future model = this.userService.updateUserProfilePicUrl(this.user.getHandle(), NEW_PIC_URL);

        assertNotEquals("Result model should not be null.", model, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateProfilePic_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.userService.updateUserProfilePicUrl("asdasdasdasd", NEW_PIC_URL);
    }

    @Test
    public void testUpdateProfilePic_WithVaidHandle_ShouldMapCorrectly() throws UserNotFoundException, ExecutionException, InterruptedException {
        Future model = this.userService.updateUserProfilePicUrl(this.user.getHandle(), NEW_PIC_URL);
        UserServiceModel resultModel = (UserServiceModel) model.get();

        assertEquals("Picture URL is not mapped correctly", resultModel.getProfile().getProfilePicUrl(), NEW_PIC_URL);
    }
}