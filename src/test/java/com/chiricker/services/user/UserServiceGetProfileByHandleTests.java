package com.chiricker.services.user;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.ProfileViewModel;
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

import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetProfileByHandleTests {

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
    private User requester;

    @Before
    public void setup() {
        this.user = new User();
        this.user.setHandle("gosho");
        this.user.setName("Gosho Ivanov");
        this.user.setProfile(new Profile());
        this.user.getProfile().setBiography("Goshos bio");
        this.user.getProfile().setWebsiteUrl("www.findAllInSet.findAllInSet");
        this.user.getProfile().setProfilePicUrl("default");
        this.user.setChiricks(new HashSet<>());
        this.user.setFollowing(new HashSet<>());
        this.user.setFollowers(new HashSet<>());

        this.requester = new User();
        this.requester.setHandle("pesho");
        this.requester.setFollowing(new HashSet<>());

        when(this.userRepository.findByHandle(this.user.getHandle())).thenReturn(this.user);
        when(this.userRepository.findByHandle(this.requester.getHandle())).thenReturn(this.requester);
    }

    @Test
    public void testGetProfileByHandle_WithValidHandles_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        ProfileViewModel result = this.userService.getProfileByHandle(this.user.getHandle(), this.requester.getHandle());

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetProfileByHandle_WithInvalidUserHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getProfileByHandle("tosho", this.requester.getHandle());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetProfileByHandle_WithInvalidRequesterHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getProfileByHandle(this.user.getHandle(), "tosho");
    }

    @Test
    public void testGetProfileByHandle_WithValidData_IsFollowingShouldBeFalse() throws UserNotFoundException {
        ProfileViewModel model = this.userService.getProfileByHandle(this.user.getHandle(), this.requester.getHandle());

        assertFalse("Requester should not be following user.", model.isFollowing());
    }

    @Test
    public void testGetProfileByHandle_WithValidData_IsFollowingShouldBeTrue() throws UserNotFoundException {
        this.requester.getFollowing().add(this.user);
        ProfileViewModel model = this.userService.getProfileByHandle(this.user.getHandle(), this.requester.getHandle());

        assertTrue("Requester should be following user.", model.isFollowing());
    }

    @Test
    public void testGetProfileByHandle_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        this.user.getChiricks().add(new Chirick());
        this.user.getChiricks().add(new Chirick());
        this.user.getFollowing().add(new User());
        this.user.getFollowers().add(new User());
        this.user.getFollowers().add(new User());

        ProfileViewModel model = this.userService.getProfileByHandle(this.user.getHandle(), this.requester.getHandle());

        assertEquals("Handle is not mapped correctly.", model.getHandle(), this.user.getHandle());
        assertEquals("Name is not mapped correctly.", model.getName(), this.user.getName());
        assertEquals("Biography is not mapped correctly.", model.getProfileBiography(), this.user.getProfile().getBiography());
        assertEquals("Website URL is not mapped correctly.", model.getProfileWebsiteUrl(), this.user.getProfile().getWebsiteUrl());
        assertEquals("Picture URL is not mapped correctly.", model.getProfilePicUrl(), this.user.getProfile().getProfilePicUrl());
        assertEquals("Chiricks count is not mapped correctly.", model.getChiricksCount(), this.user.getChiricks().size());
        assertEquals("Followers is not mapped correctly.", model.getFollowersCount(), this.user.getFollowers().size());
        assertEquals("Following is not mapped correctly.", model.getFollowingCount(), this.user.getFollowing().size());
    }
}
