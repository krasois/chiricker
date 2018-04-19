package com.chiricker.services.user;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
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
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceEditAdminTests {

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

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

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

    private EditUserBindingModel testModel;
    private User testUser;

    private RoleServiceModel userRole;
    private RoleServiceModel adminRole;

    @Before
    public void setup() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setHandle(USER_HANDLE);
        testUser.setName(USER_NAME);
        testUser.setPassword(USER_PASSWORD);
        testUser.setEmail(USER_EMAIL);
        testUser.setEnabled(true);
        testUser.setCredentialsNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setAccountNonExpired(true);
        testUser.setProfile(new Profile() {{
            setBiography(USER_BIO);
            setProfilePicUrl(USER_PIC_URL);
        }});
        testUser.setAuthorities(new HashSet<>());

        this.testModel = new EditUserBindingModel();
        this.testModel.setEmail(USER_NEW_EMAIL);
        this.testModel.setName(USER_NEW_NAME);
        this.testModel.setProfileBiography(USER_NEW_BIO);

        this.testModel.setHandle(USER_HANDLE);
        this.testModel.setProfileWebsiteUrl("");
        this.testModel.setPassword(USER_PASSWORD);
        this.testModel.setConfirmPassword("");
        this.testModel.setProfilePicture(null);
        this.testModel.setAuthorities(new HashSet<>() {{
            add(ROLE_USER);
            add(ROLE_ADMIN);
        }});

        this.userRole = new RoleServiceModel();
        this.userRole.setAuthority(ROLE_USER);

        this.adminRole = new RoleServiceModel();
        this.adminRole.setAuthority(ROLE_ADMIN);

        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(this.testUser));
        when(this.userRepository.save(any())).thenAnswer(u -> u.getArgument(0));
        when(this.roleService.getRoleByName("ROLE_USER")).thenReturn(this.userRole);
        when(this.roleService.getRoleByName("ROLE_ADMIN")).thenReturn(this.adminRole);
        when(mapper.map(any(User.class), eq(UserServiceModel.class))).thenAnswer(r ->  {
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
        when(this.mapper.map(any(RoleServiceModel.class), eq(Role.class))).thenAnswer(a -> {
            RoleServiceModel m = a.getArgument(0);
            Role role = new Role();
            role.setAuthority(m.getAuthority());
            return role;
        });
    }

    @Test
    public void testEdit_WithValidModel_ShouldNotReturnNull() throws UserNotFoundException, UserRoleNotFoundException {
        UserServiceModel user = this.userService.editAdmin(this.testUser.getId(), this.testModel);

        Assert.assertNotEquals("User should not be null.", user, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testEdit_WithInvalidHandle_ShouldThrow() throws UserNotFoundException, UserRoleNotFoundException {
        this.userService.editAdmin("aaddddasdas", this.testModel);
    }

    @Test
    public void testEdit_WithChangedPassword_ShouldBeHashed() throws UserNotFoundException, UserRoleNotFoundException {
        when(this.passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
        when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserServiceModel user = this.userService.editAdmin(this.testUser.getId(), this.testModel);

        Assert.assertEquals("Password was not hashed.", user.getPassword(), HASHED_PASSWORD);
    }

    @Test
    public void testEdit_WithUnchangedPassword_ShouldNotBeHashed() throws UserNotFoundException, UserRoleNotFoundException {
        this.testModel.setPassword("");
        UserServiceModel user = this.userService.editAdmin(this.testUser.getId(), this.testModel);

        Assert.assertEquals("Password was not hashed or null.", user.getPassword(), USER_PASSWORD);
    }

    @Test
    public void testEdit_WithValidModel_ShouldMapCorrectly() throws UserNotFoundException, UserRoleNotFoundException {
        when(this.passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
        when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        UserServiceModel user = this.userService.editAdmin(this.testUser.getId(), this.testModel);

        Assert.assertEquals("User name should be mapped correctly.", user.getName(), this.testModel.getName());
        Assert.assertEquals("User handle should be mapped correctly.", user.getHandle(), this.testModel.getHandle());
        Assert.assertEquals("User email should be mapped correctly.", user.getEmail(), this.testModel.getEmail());
        Assert.assertEquals("User password should be mapped correctly.", user.getPassword(), HASHED_PASSWORD);
        Assert.assertEquals("User bio should be mapped correctly.", user.getProfile().getBiography(), this.testModel.getProfileBiography());
        Assert.assertEquals("User website should be mapped correctly.", user.getProfile().getWebsiteUrl(), this.testModel.getProfileWebsiteUrl());
        Assert.assertTrue("User has more than 2 roles after edit", user.getAuthorities().size() == 2);
        Assert.assertTrue("User does not have user role.", user.getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_USER)));
        Assert.assertTrue("User does not have admin role.", user.getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN)));
    }
}
