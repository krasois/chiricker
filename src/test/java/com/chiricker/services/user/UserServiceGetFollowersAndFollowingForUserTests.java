package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.FollowerViewModel;
import com.chiricker.areas.users.models.view.ProfileViewModel;
import com.chiricker.areas.users.repositories.UserRepository;
import com.chiricker.areas.users.services.role.RoleService;
import com.chiricker.areas.users.services.user.UserServiceImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetFollowersAndFollowingForUserTests {

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
    private Pageable pageable;
    private List<User> followers;
    private User tosho;

    @Before
    public void setup() {
        this.user = new User();
        this.user.setHandle("gosho");

        this.tosho = new User();
        this.tosho.setHandle("tosho");
        this.tosho.setName("Tpsho");
        this.tosho.setProfile(new Profile());
        this.tosho.getProfile().setBiography("toshos bio");
        this.tosho.getProfile().setProfilePicUrl("www.basicpic.com/tosho");
        this.tosho.setFollowing(new HashSet<>());

        this.requester = new User();
        this.requester.setHandle("pesho");
        this.requester.setName("PEsho");
        this.requester.setProfile(new Profile());
        this.requester.getProfile().setBiography("peshos bio");
        this.requester.getProfile().setProfilePicUrl("www.basicpic.com");
        this.requester.setFollowing(new HashSet<>() {{ add(tosho); }});

        this.pageable = Mockito.mock(Pageable.class);

        this.followers = new ArrayList<>();
        this.followers.add(this.requester);
        this.followers.add(this.tosho);

        when(this.userRepository.findByHandle(this.user.getHandle())).thenReturn(this.user);
        when(this.userRepository.findByHandle(this.requester.getHandle())).thenReturn(this.requester);
        when(this.userRepository.findAllByFollowingContainingOrderByHandle(this.user, this.pageable)).thenReturn(this.followers);
        when(this.userRepository.findAllByFollowersContainingOrderByHandle(this.user, this.pageable)).thenReturn(this.followers);
        when(this.mapper.map(any(), eq(FollowerViewModel.class))).thenAnswer(a -> {
            User aUser = a.getArgument(0);
            FollowerViewModel model = new FollowerViewModel();
            model.setHandle(aUser.getHandle());
            model.setName(aUser.getName());
            model.setProfileBiography(aUser.getProfile().getBiography());
            model.setProfilePicUrl(aUser.getProfile().getProfilePicUrl());

            return model;
        });
    }

    @Test
    public void testGetFollowersForUser_WithValidHandles_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        List<FollowerViewModel> result = this.userService.getFollowersForUser(this.user.getHandle(), this.requester.getHandle(), this.pageable);

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test
    public void testGetFollowingForUser_WithValidHandles_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        List<FollowerViewModel> result = this.userService.getFollowingForUser(this.user.getHandle(), this.requester.getHandle(), this.pageable);

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetFollowersForUser_WithInvalidUserHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getFollowersForUser("tosho", this.requester.getHandle(), this.pageable);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetFollowingForUser_WithInvalidUserHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getFollowingForUser("tosho", this.requester.getHandle(), this.pageable);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetFollowersForUser_WithInvalidRequesterHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getFollowersForUser(this.user.getHandle(), "tosho", this.pageable);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetFollowingForUser_WithInvalidRequesterHandlde_ShoudThrow() throws UserNotFoundException {
        this.userService.getFollowingForUser(this.user.getHandle(), "tosho", this.pageable);
    }

    @Test
    public void testGetFollowersForUser_WithValidHandles_ShouldMapCorrectly() throws UserNotFoundException {
        List<FollowerViewModel> followers = this.userService.getFollowersForUser(this.user.getHandle(), this.requester.getHandle(), this.pageable);
        this.testMappings(followers);
    }

    @Test
    public void testGetFollowingForUser_WithValidHandles_ShouldMapCorrectly() throws UserNotFoundException {
        List<FollowerViewModel> following = this.userService.getFollowingForUser(this.user.getHandle(), this.requester.getHandle(), this.pageable);
        this.testMappings(following);
    }

    private void testMappings(List<FollowerViewModel> models) {
        FollowerViewModel requesterModel = models.get(0);
        FollowerViewModel toshoModel = models.get(1);

        assertEquals("Handle not mapped correctly.", requesterModel.getHandle(), this.requester.getHandle());
        assertEquals("Name not mapped correctly.", requesterModel.getHandle(), this.requester.getHandle());
        assertEquals("Biography not mapped correctly.", requesterModel.getProfileBiography(), this.requester.getProfile().getBiography());
        assertEquals("Picture URL not mapped correctly.", requesterModel.getProfilePicUrl(), this.requester.getProfile().getProfilePicUrl());
        assertFalse("Requester should be following the user.", requesterModel.isFollowed());
        assertTrue("Requester should be themselves.", requesterModel.isSelf());

        assertEquals("Handle not mapped correctly.", toshoModel.getHandle(), this.tosho.getHandle());
        assertEquals("Name not mapped correctly.", toshoModel.getHandle(), this.tosho.getHandle());
        assertEquals("Biography not mapped correctly.", toshoModel.getProfileBiography(), this.tosho.getProfile().getBiography());
        assertEquals("Picture URL not mapped correctly.", toshoModel.getProfilePicUrl(), this.tosho.getProfile().getProfilePicUrl());
        assertTrue("Requester should be following the user.", toshoModel.isFollowed());
        assertFalse("Requester should not be themselves.", toshoModel.isSelf());
    }
}