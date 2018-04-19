package com.chiricker.services.user;

import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.service.TimelineServiceModel;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.ProfileServiceModel;
import com.chiricker.areas.users.models.service.RoleServiceModel;
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

    private User user;

    @Before
    public void setup() {
        user = new User();

        Mockito.when(this.userRepository.findByHandle(USER_HANDLE)).thenReturn(this.user);
        Role userRole = new Role(){{
            setId(3L);
            setAuthority(ROLE_USER);
        }};

        user.setId(USER_ID);
        user.setHandle(USER_HANDLE);
        user.setName("PEsho");
        user.setEmail("pesho@findAllInSet.as");
        user.setPassword("peshospass213");
        user.setAuthorities(new HashSet<>(){{ add(userRole); }});
        user.setRegisteredOn(new Date());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setProfile(new Profile() {{
            setId("dfh2w-gdhh4-fgdff3-fghjwg3");
            setProfilePicUrl("dropbox.com/chiricker/dghdh0dhd7hdg7d9");
            setWebsiteUrl("www.website.com");
            setBiography("Peshos very interestring bio");
        }});
        user.setTimeline(new Timeline(){{
            setId("557-44774-sdsdg");
            setUser(user);
        }});

        Mockito.when(mapper.map(ArgumentMatchers.any(), ArgumentMatchers.eq(UserServiceModel.class))).thenAnswer(r -> {
            User dbUser = r.getArgument(0);
            UserServiceModel model = new UserServiceModel();

            Set<RoleServiceModel> mappedAuthorities = new HashSet<>() {{
                add(new RoleServiceModel() {{
                    setId(userRole.getId());
                    setAuthority(userRole.getAuthority());
                }});
            }};
            ProfileServiceModel mappedProfile = new ProfileServiceModel() {{
                setId(dbUser.getProfile().getId());
                setBiography(dbUser.getProfile().getBiography());
                setProfilePicUrl(dbUser.getProfile().getProfilePicUrl());
                setWebsiteUrl(dbUser.getProfile().getWebsiteUrl());
            }};
            TimelineServiceModel mappedTimeline = new TimelineServiceModel() {{
                setId(dbUser.getTimeline().getId());
                setUser(model);
                setPosts(new HashSet<>());
            }};

            model.setId(dbUser.getId());
            model.setHandle(dbUser.getHandle());
            model.setName(dbUser.getName());
            model.setEmail(dbUser.getEmail());
            model.setPassword(dbUser.getPassword());
            model.setAuthorities(mappedAuthorities);
            model.setRegisteredOn(dbUser.getRegisteredOn());
            model.setEnabled(dbUser.isEnabled());
            model.setAccountNonExpired(dbUser.isAccountNonExpired());
            model.setAccountNonLocked(dbUser.isAccountNonLocked());
            model.setCredentialsNonExpired(dbUser.isCredentialsNonExpired());
            model.setProfile(mappedProfile);
            model.setTimeline(mappedTimeline);
            return model;
        });
    }

    @Test
    public void testGetByHandle_WithValidHandle_ShouldNotReturnNull() {
        UserServiceModel userModel = this.userService.getByHandle("pesho");

        Assert.assertNotEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandle_WithInvalidHandle_ShouldReturnNull() {
        UserServiceModel userModel = this.userService.getByHandle("gosho");

        Assert.assertEquals("User model should not be null.", null, userModel);
    }

    @Test
    public void testGetByHandle_WithValidHandle_ShouldMapFieldsCorrectly() {
        UserServiceModel userModel = this.userService.getByHandle("pesho");

        Assert.assertEquals("User Id is not mapped correctly.", this.user.getId(), userModel.getId());
        Assert.assertEquals("User handle is not mapped correctly.", this.user.getHandle(), userModel.getHandle());
        Assert.assertEquals("User name is not mapped correctly.", this.user.getName(), userModel.getName());
        Assert.assertEquals("User email is not mapped correctly.", this.user.getEmail(), userModel.getEmail());
        Assert.assertEquals("User password is not mapped correctly.", this.user.getPassword(), userModel.getPassword());
        Assert.assertEquals("User register date is not mapped correctly.", this.user.getRegisteredOn(), userModel.getRegisteredOn());
        Assert.assertEquals("User enabled is not mapped correctly.", this.user.isEnabled(), userModel.isEnabled());
        Assert.assertEquals("User account non expired is not mapped correctly.", this.user.isAccountNonExpired(), userModel.isAccountNonExpired());
        Assert.assertEquals("User account non locked is not mapped correctly.", this.user.isAccountNonLocked(), userModel.isAccountNonLocked());
        Assert.assertEquals("User credentials non expired is not mapped correctly.", this.user.isCredentialsNonExpired(), userModel.isCredentialsNonExpired());

        Assert.assertEquals("User profile Id is not mapped correctly.", this.user.getProfile().getId(), userModel.getProfile().getId());
        Assert.assertEquals("User profile biography is not mapped correctly.", this.user.getProfile().getBiography(), userModel.getProfile().getBiography());
        Assert.assertEquals("User profile website url is not mapped correctly.", this.user.getProfile().getWebsiteUrl(), userModel.getProfile().getWebsiteUrl());
        Assert.assertEquals("User profile picture url is not mapped correctly.", this.user.getProfile().getProfilePicUrl(), userModel.getProfile().getProfilePicUrl());

        Assert.assertEquals("User timeline Id is not mapped correctly.", this.user.getTimeline().getId(), userModel.getTimeline().getId());
        Assert.assertEquals("User timeline user Id is not mapped correctly.", this.user.getTimeline().getUser().getId(), userModel.getTimeline().getUser().getId());

        Assert.assertEquals("User has more or less roles than he should.", this.user.getAuthorities().size(), userModel.getAuthorities().size());
        Assert.assertTrue("User role is not mapped correctly.", userModel
                .getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority()
                        .equals(ROLE_USER)));
    }
}