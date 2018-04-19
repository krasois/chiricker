package com.chiricker.services.user;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.Role;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceGetUserSettingsAdminTests {

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
        this.user.setId("asda7g3-v34v3vab-sdtg325-dhghd4t3");
        this.user.setHandle("pesho");
        this.user.setName("Pesho");
        this.user.setEmail("asdsa@adsd.findAllInSet");
        this.user.setProfile(new Profile());
        this.user.getProfile().setBiography("grgrgrasgfafgghhth");
        this.user.getProfile().setWebsiteUrl("www.website.com");
        this.user.getProfile().setProfilePicUrl("default");
        this.user.setAuthorities(new HashSet<>() {{
            add(new Role() {{ setAuthority("ROLE_USER"); }});
        }});

        when(this.userRepository.findById(this.user.getId())).thenReturn(Optional.of(this.user));
        when(this.mapper.map(any(User.class), eq(EditUserBindingModel.class))).thenAnswer(a -> {
            User u = a.getArgument(0);
            EditUserBindingModel model = new EditUserBindingModel();
            model.setId(u.getId());
            model.setHandle(u.getHandle());
            model.setName(u.getName());
            model.setProfileBiography(u.getProfile().getBiography());
            model.setProfilePicUrl(u.getProfile().getProfilePicUrl());
            model.setProfileWebsiteUrl(u.getProfile().getWebsiteUrl());
            model.setEmail(u.getEmail());
            model.setAuthorities(u.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet()));
            return model;
        });
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        EditUserBindingModel result = this.userService.getUserSettingsAdmin(this.user.getId());

        assertNotEquals("Result should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetSettings_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.userService.getUserSettings("gasd23h-onyh25twr26-sh3t3w34-ogwe4f3f");
    }

    @Test
    public void testGetSettings_WithValidHandle_ShouldMapCorrectly() throws UserNotFoundException {
        EditUserBindingModel model = this.userService.getUserSettingsAdmin(this.user.getId());

        assertEquals("Id was not mapped correctly.", model.getId(), this.user.getId());
        assertEquals("Handle was not mapped correctly.", model.getHandle(), this.user.getHandle());
        assertEquals("Name was not mapped correctly.", model.getName(), this.user.getName());
        assertEquals("Email was not mapped correctly.", model.getEmail(), this.user.getEmail());
        assertEquals("Biography was not mapped correctly.", model.getProfileBiography(), this.user.getProfile().getBiography());
        assertEquals("Website URL was not mapped correctly.", model.getProfileWebsiteUrl(), this.user.getProfile().getWebsiteUrl());
        assertEquals("Profile pic URL was not mapped correctly.", model.getProfilePicUrl(), this.user.getProfile().getProfilePicUrl());
        assertTrue("User has more or less that 1 role.", model.getAuthorities().size() == 1);
        assertTrue("User does not have user role.", model.getAuthorities().contains("ROLE_USER"));
    }
}
