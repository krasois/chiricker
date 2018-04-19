package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.view.FollowResultViewModel;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceFollowTests {

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

    private FollowBindingModel testModel;
    private User pesho;
    private User gosho;

    @Before
    public void setup() {
        this.testModel = new FollowBindingModel();

        this.pesho = new User();
        this.pesho.setHandle("pesho");
        this.pesho.setFollowing(new HashSet<>());

        this.gosho = new User();
        this.gosho.setHandle("gosho");

        when(this.userRepository.findByHandle(this.pesho.getHandle())).thenReturn(this.pesho);
        when(this.userRepository.findByHandle(this.gosho.getHandle())).thenReturn(this.gosho);
        when(this.userRepository.save(any())).thenAnswer(u -> u.getArgument(0));
    }

    @Test
    public void testFollow_WithTwoValidHandles_ShouldNotThrowOrReturnNull() throws UserNotFoundException {
        this.testModel.setHandle(this.gosho.getHandle());
        FollowResultViewModel result = this.userService.follow(this.testModel, this.pesho.getHandle());

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testFollow_WithWrongUserHandle_ShouldThrow() throws UserNotFoundException {
        this.testModel.setHandle("tosho");
        this.userService.follow(this.testModel, this.pesho.getHandle());
    }

    @Test(expected = UserNotFoundException.class)
    public void testFollow_WithWrongRequesterHandle_ShouldThrow() throws UserNotFoundException {
        this.testModel.setHandle(this.gosho.getHandle());
        this.userService.follow(this.testModel, "tosho");
    }

    @Test(expected = IllegalStateException.class)
    public void testFollow_WithUserEqualsRequester_ShouldThrow() throws UserNotFoundException {
        this.testModel.setHandle(this.pesho.getHandle());
        this.userService.follow(this.testModel, this.pesho.getHandle());
    }

    @Test()
    public void testFollow_WithValidData_ShouldReturnTrueForFollowed() throws UserNotFoundException {
        this.testModel.setHandle(this.gosho.getHandle());
        FollowResultViewModel result = this.userService.follow(this.testModel, this.pesho.getHandle());

        assertFalse("Should have followed", result.isUnfollowed());
    }

    @Test()
    public void testFollow_WithValidData_ShouldReturnFalseForFollowed() throws UserNotFoundException {
        this.testModel.setHandle(this.gosho.getHandle());
        this.pesho.getFollowing().add(this.gosho);
        FollowResultViewModel result = this.userService.follow(this.testModel, this.pesho.getHandle());

        assertTrue("Should have followed", result.isUnfollowed());
    }
}