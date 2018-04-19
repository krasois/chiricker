package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.UserNavbarViewModel;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetNavbarInfoTests {

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

    private User userTest;

    @Before
    public void setup() {
        this.userTest = new User();
        this.userTest.setHandle("pesho");
        this.userTest.setName("Pesho");
        this.userTest.setProfile(new Profile());
        this.userTest.getProfile().setProfilePicUrl("default");

        when(this.userRepository.findByHandle(this.userTest.getHandle())).thenReturn(this.userTest);
        when(this.mapper.map(any(), eq(UserNavbarViewModel.class))).thenAnswer(m -> {
            User user = m.getArgument(0);

            UserNavbarViewModel nav = new UserNavbarViewModel();
            nav.setHandle(user.getHandle());
            nav.setName(user.getName());
            nav.setProfilePicUrl(user.getProfile().getProfilePicUrl());

            return nav;
        });
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        UserNavbarViewModel result = this.userService.getNavbarInfo(this.userTest.getHandle());

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetSettings_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.userService.getUserCard("gosho");
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldMapCorrectly() throws UserNotFoundException {
        UserNavbarViewModel model = this.userService.getNavbarInfo(this.userTest.getHandle());

        assertEquals("Handle was not mapped correctly.", model.getHandle(), this.userTest.getHandle());
        assertEquals("Name was not mapped correctly.", model.getName(), this.userTest.getName());
        assertEquals("Profile pic URL was not mapped correctly.", model.getProfilePicUrl(), this.userTest.getProfile().getProfilePicUrl());
    }
}
