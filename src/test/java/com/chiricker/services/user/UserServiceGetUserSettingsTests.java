package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetUserSettingsTests {

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
        this.user.setName("Pesho");
        this.user.setEmail("asdsa@adsd.findAllInSet");
        this.user.setProfile(new Profile());
        this.user.getProfile().setBiography("grgrgrasgfafgghhth");
        this.user.getProfile().setWebsiteUrl("www.website.com");
        this.user.getProfile().setProfilePicUrl("default");

        when(this.userRepository.findByIsEnabledIsTrueAndHandle(this.user.getHandle())).thenReturn(this.user);
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        UserEditBindingModel result = this.userService.getUserSettings(this.user.getHandle());

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetSettings_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.userService.getUserSettings("gosho");
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldMapCorrectly() throws UserNotFoundException {
        UserEditBindingModel model = this.userService.getUserSettings(this.user.getHandle());

        assertEquals("Handle was not mapped correctly.", model.getHandle(), this.user.getHandle());
        assertEquals("Name was not mapped correctly.", model.getName(), this.user.getName());
        assertEquals("Email was not mapped correctly.", model.getEmail(), this.user.getEmail());
        assertEquals("Biography was not mapped correctly.", model.getBiography(), this.user.getProfile().getBiography());
        assertEquals("Website URL was not mapped correctly.", model.getWebsiteUrl(), this.user.getProfile().getWebsiteUrl());
        assertEquals("Profile pic URL was not mapped correctly.", model.getPictureUrl(), this.user.getProfile().getProfilePicUrl());
    }
}