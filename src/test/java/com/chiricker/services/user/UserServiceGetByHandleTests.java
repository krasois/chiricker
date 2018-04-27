package com.chiricker.services.user;

import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.service.TimelineServiceModel;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.ProfileServiceModel;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.repositories.UserRepository;
import com.chiricker.areas.users.services.role.RoleService;
import com.chiricker.areas.users.services.user.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetByHandleTests {

    private static final String USER_ID = "2hffgh4n-fddg34hg-sggss-234";
    private static final String USER_HANDLE = "pesho";
    private static final String ROLE_USER = "ROLE_USER";

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

    private SimpleUserServiceModel user;
    private UserServiceModel user2;

    @Before
    public void setup() {
        user = new SimpleUserServiceModel();
        user2 = new UserServiceModel();

        RoleServiceModel userRole = new RoleServiceModel(){{
            setId(3L);
            setAuthority(ROLE_USER);
        }};

        ProfileServiceModel profile = new ProfileServiceModel() {{
            setId("dfh2w-gdhh4-fgdff3-fghjwg3");
            setProfilePicUrl("dropbox.com/chiricker/dghdh0dhd7hdg7d9");
            setWebsiteUrl("www.website.com");
            setBiography("Peshos very interestring bio");
        }};

        user.setId(USER_ID);
        user.setHandle(USER_HANDLE);
        user.setName("PEsho");
        user.setEmail("pesho@findAllInSet.as");
        user.setPassword("peshospass213");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setProfile(profile);

        user2.setId(USER_ID);
        user2.setHandle(USER_HANDLE);
        user2.setName("PEsho");
        user2.setEmail("pesho@findAllInSet.as");
        user2.setPassword("peshospass213");
        user2.setAuthorities(new HashSet<>(){{ add(userRole); }});
        user2.setRegisteredOn(new Date());
        user2.setEnabled(true);
        user2.setAccountNonExpired(true);
        user2.setAccountNonLocked(true);
        user2.setCredentialsNonExpired(true);
        user2.setProfile(profile);
        user2.setTimeline(new TimelineServiceModel(){{
            setId("557-44774-sdsdg");
            setUser(user2);
        }});

        when(this.userRepository.findByHandle(USER_HANDLE)).thenReturn(new User());
        when(this.mapper.map(any(User.class), eq(SimpleUserServiceModel.class))).thenReturn(this.user);
        when(this.mapper.map(any(User.class), eq(UserServiceModel.class))).thenReturn(this.user2);
    }

    @Test
    public void testGetByHandleSimple_WithValidHandle_ShouldNotReturnNull() {
        SimpleUserServiceModel userModel = this.userService.getByHandleSimple("pesho");

        Assert.assertNotEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandleModel_WithValidHandle_ShouldNotReturnNull() {
        UserServiceModel userModel = this.userService.getByHandleModel("pesho");

        Assert.assertNotEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandleSimple_WithInvalidHandle_ShouldReturnNull() {
        SimpleUserServiceModel userModel = this.userService.getByHandleSimple("gosho");

        Assert.assertEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandleModel_WithInvalidHandle_ShouldReturnNull() {
        UserServiceModel userModel = this.userService.getByHandleModel("gosho");

        Assert.assertEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandleSimple_WithValidHandle_ShouldMapFieldsCorrectly() {
        SimpleUserServiceModel userModel = this.userService.getByHandleSimple("pesho");

        Assert.assertEquals("User Id is not mapped correctly.", this.user.getId(), userModel.getId());
        Assert.assertEquals("User handle is not mapped correctly.", this.user.getHandle(), userModel.getHandle());
        Assert.assertEquals("User name is not mapped correctly.", this.user.getName(), userModel.getName());
        Assert.assertEquals("User email is not mapped correctly.", this.user.getEmail(), userModel.getEmail());
        Assert.assertEquals("User password is not mapped correctly.", this.user.getPassword(), userModel.getPassword());
        Assert.assertEquals("User enabled is not mapped correctly.", this.user.isEnabled(), userModel.isEnabled());
        Assert.assertEquals("User account non expired is not mapped correctly.", this.user.isAccountNonExpired(), userModel.isAccountNonExpired());
        Assert.assertEquals("User account non locked is not mapped correctly.", this.user.isAccountNonLocked(), userModel.isAccountNonLocked());
        Assert.assertEquals("User credentials non expired is not mapped correctly.", this.user.isCredentialsNonExpired(), userModel.isCredentialsNonExpired());

        Assert.assertEquals("User profile Id is not mapped correctly.", this.user.getProfile().getId(), userModel.getProfile().getId());
        Assert.assertEquals("User profile biography is not mapped correctly.", this.user.getProfile().getBiography(), userModel.getProfile().getBiography());
        Assert.assertEquals("User profile website url is not mapped correctly.", this.user.getProfile().getWebsiteUrl(), userModel.getProfile().getWebsiteUrl());
        Assert.assertEquals("User profile picture url is not mapped correctly.", this.user.getProfile().getProfilePicUrl(), userModel.getProfile().getProfilePicUrl());
    }

    @Test
    public void testGetByHandleModel_WithValidHandle_ShouldMapFieldsCorrectly() {
        UserServiceModel userModel = this.userService.getByHandleModel("pesho");

        Assert.assertEquals("User Id is not mapped correctly.", this.user2.getId(), userModel.getId());
        Assert.assertEquals("User handle is not mapped correctly.", this.user2.getHandle(), userModel.getHandle());
        Assert.assertEquals("User name is not mapped correctly.", this.user2.getName(), userModel.getName());
        Assert.assertEquals("User email is not mapped correctly.", this.user2.getEmail(), userModel.getEmail());
        Assert.assertEquals("User password is not mapped correctly.", this.user2.getPassword(), userModel.getPassword());
        Assert.assertEquals("User enabled is not mapped correctly.", this.user2.isEnabled(), userModel.isEnabled());
        Assert.assertEquals("User account non expired is not mapped correctly.", this.user2.isAccountNonExpired(), userModel.isAccountNonExpired());
        Assert.assertEquals("User account non locked is not mapped correctly.", this.user2.isAccountNonLocked(), userModel.isAccountNonLocked());
        Assert.assertEquals("User credentials non expired is not mapped correctly.", this.user2.isCredentialsNonExpired(), userModel.isCredentialsNonExpired());

        Assert.assertEquals("User profile Id is not mapped correctly.", this.user2.getProfile().getId(), userModel.getProfile().getId());
        Assert.assertEquals("User profile biography is not mapped correctly.", this.user2.getProfile().getBiography(), userModel.getProfile().getBiography());
        Assert.assertEquals("User profile website url is not mapped correctly.", this.user2.getProfile().getWebsiteUrl(), userModel.getProfile().getWebsiteUrl());
        Assert.assertEquals("User profile picture url is not mapped correctly.", this.user2.getProfile().getProfilePicUrl(), userModel.getProfile().getProfilePicUrl());

        Assert.assertEquals("User timeline Id is not mapped correctly.", this.user2.getTimeline().getId(), userModel.getTimeline().getId());
        Assert.assertEquals("User timeline user Id is not mapped correctly.", this.user2.getTimeline().getUser().getId(), userModel.getTimeline().getUser().getId());

        Assert.assertEquals("User has more or less roles than he should.", this.user2.getAuthorities().size(), userModel.getAuthorities().size());
        Assert.assertTrue("User role is not mapped correctly.", userModel
                .getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority()
                        .equals(ROLE_USER)));
    }
}