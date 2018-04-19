package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.entities.Profile;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceEditTests {

    private static final String USER_PASSWORD = "as$asdff$sdgw3qg3$afe3wEF$";
    private static final String HASHED_PASSWORD = "someHashedPassword";
    private static final String USER_NEW_EMAIL = "pesho@abv.bg";
    private static final String USER_NEW_NAME = "Pesho Peshov";
    private static final String USER_NEW_BIO = "Bio";

    private static final String USER_ID = "234hh-3422dgd-dhdfh4db2-dfdh4";
    private static final String USER_HANDLE = "pesho";
    private static final String USER_NAME = "Pesho Peshinski";
    private static final String USER_EMAIL = "pesho@findAllInSet.ds";
    private static final String USER_BIO = "Peshos life in a nutshell.";
    private static final String USER_PIC_URL = "default";


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

    private UserEditBindingModel testModel;

    @Before
    public void setup() {
        User pesho = new User();
        pesho.setId(USER_ID);
        pesho.setHandle(USER_HANDLE);
        pesho.setName(USER_NAME);
        pesho.setPassword(USER_PASSWORD);
        pesho.setEmail(USER_EMAIL);
        pesho.setEnabled(true);
        pesho.setCredentialsNonExpired(true);
        pesho.setAccountNonLocked(true);
        pesho.setAccountNonExpired(true);
        pesho.setProfile(new Profile() {{
            setBiography(USER_BIO);
            setProfilePicUrl(USER_PIC_URL);
        }});
        pesho.setAuthorities(new HashSet<>());

        this.testModel = new UserEditBindingModel();
        this.testModel.setEmail(USER_NEW_EMAIL);
        this.testModel.setName(USER_NEW_NAME);
        this.testModel.setBiography(USER_NEW_BIO);

        this.testModel.setHandle(USER_HANDLE);
        this.testModel.setWebsiteUrl("");
        this.testModel.setPassword(USER_PASSWORD);
        this.testModel.setConfirmPassword("");
        this.testModel.setProfilePicture(null);

        Mockito.when(this.userRepository.findByHandle("pesho")).thenReturn(pesho);
        Mockito.when(this.userRepository.save(any())).thenAnswer(u -> u.getArgument(0));
        Mockito.when(mapper.map(any(User.class), eq(UserServiceModel.class))).thenAnswer(r ->  {
            User model = r.getArgument(0);
            UserServiceModel userModel = new UserServiceModel();

            userModel.setHandle(model.getHandle());
            userModel.setName(model.getName());
            userModel.setEmail(model.getEmail());
            userModel.setPassword(model.getPassword());
            userModel.setEnabled(model.isEnabled());
            userModel.setAccountNonLocked(model.isAccountNonLocked());
            userModel.setAccountNonExpired(model.isAccountNonExpired());
            userModel.setCredentialsNonExpired(model.isCredentialsNonExpired());
            userModel.setProfile(new ProfileServiceModel() {{
                setBiography(model.getProfile().getBiography());
                setWebsiteUrl(model.getProfile().getWebsiteUrl());
            }});

            Set<RoleServiceModel> roles = new HashSet<>();
            for (GrantedAuthority grantedAuthority : model.getAuthorities()) {
                roles.add(new RoleServiceModel() {{
                    setAuthority(grantedAuthority.getAuthority());
                }});
            }

            userModel.setAuthorities(roles);
            return userModel;
        });
    }

    @Test
    public void testEdit_WithValidModel_ShouldNotReturnNull() throws UserNotFoundException {
        UserServiceModel user = this.userService.edit(this.testModel, "pesho");

        Assert.assertNotEquals("User should not be null.", user, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testEdit_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.userService.edit(this.testModel, "gosho");
    }

    @Test
    public void testEdit_WithChangedPassword_ShouldBeHashed() throws UserNotFoundException {
        Mockito.when(this.passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
        Mockito.when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserServiceModel user = this.userService.edit(this.testModel, "pesho");

        Assert.assertEquals("Password was not hashed.", user.getPassword(), HASHED_PASSWORD);
    }

    @Test
    public void testEdit_WithUnchangedPassword_ShouldNotBeHashed() throws UserNotFoundException {
        this.testModel.setPassword("");
        UserServiceModel user = this.userService.edit(this.testModel, "pesho");

        Assert.assertEquals("Password was not hashed or null.", user.getPassword(), USER_PASSWORD);
    }

    @Test
    public void testEdit_WithValidModel_ShouldMapCorrectly() throws UserNotFoundException {
        Mockito.when(this.passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
        Mockito.when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserServiceModel user = this.userService.edit(this.testModel, "pesho");

        Assert.assertEquals("User name should be mapped correctly.", user.getName(), this.testModel.getName());
        Assert.assertEquals("User handle should be mapped correctly.", user.getHandle(), this.testModel.getHandle());
        Assert.assertEquals("User email should be mapped correctly.", user.getEmail(), this.testModel.getEmail());
        Assert.assertEquals("User password should be mapped correctly.", user.getPassword(), HASHED_PASSWORD);
        Assert.assertEquals("User bio should be mapped correctly.", user.getProfile().getBiography(), this.testModel.getBiography());
        Assert.assertEquals("User website should be mapped correctly.", user.getProfile().getWebsiteUrl(), this.testModel.getWebsiteUrl());
    }
}