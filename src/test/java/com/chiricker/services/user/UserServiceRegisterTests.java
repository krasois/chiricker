package com.chiricker.services.user;

import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.entities.User;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceRegisterTests {

    private static final String HASHED_PASSWORD = "s0M3H4ShedPaSsw0rD";
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

    private UserRegisterBindingModel testModel;
    private RoleServiceModel roleModel;

    @Before
    public void setup() {
        this.testModel = new UserRegisterBindingModel();
        this.testModel.setName("Pesho");
        this.testModel.setHandle("pesho");
        this.testModel.setEmail("pesho@findAllInSet.sd");
        this.testModel.setPassword("pesho123");

        this.roleModel = new RoleServiceModel() {{
            setId(1L);
            setAuthority(ROLE_USER);
        }};

        Mockito.when(this.userRepository.save(any())).thenAnswer(u -> u.getArgument(0));
        Mockito.when(this.roleService.getRoleByName("ROLE_USER")).thenReturn(roleModel);
        Mockito.when(this.passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
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

            Set<RoleServiceModel> roles = new HashSet<>();
            for (GrantedAuthority grantedAuthority : model.getAuthorities()) {
                roles.add(new RoleServiceModel() {{
                    setAuthority(grantedAuthority.getAuthority());
                }});
            }

            userModel.setAuthorities(roles);
            return userModel;
        });

        Mockito.when(this.mapper.map(any(UserRegisterBindingModel.class), eq(User.class))).thenAnswer(m -> {
            UserRegisterBindingModel model = m.getArgument(0);
            User user = new User();

            user.setName(model.getName());
            user.setHandle(model.getHandle());
            user.setPassword(model.getPassword());
            user.setEmail(model.getEmail());

            return user;
        });

        Mockito.when(this.mapper.map(any(RoleServiceModel.class), eq(Role.class))).thenAnswer(r ->  {
            RoleServiceModel model = r.getArgument(0);
            Role role = new Role();

            role.setId(model.getId());
            role.setAuthority(model.getAuthority());

            return role;
        });
    }

    @Test
    public void testRegister_WithValidUser_ShouldNotReturnNull () throws UserRoleNotFoundException {
        UserServiceModel model = this.userService.register(this.testModel);

        Assert.assertNotEquals("User should not be null.", null, model);
    }

    @Test
    public void testRegister_WithValidUser_PasswordShouldBeHashed() throws UserRoleNotFoundException {
        UserServiceModel model = this.userService.register(this.testModel);

        assertEquals("Password was not encoded properly", HASHED_PASSWORD, model.getPassword());
    }

    @Test
    public void testRegister_WithValidUser_ShouldMapCorrectly () throws UserRoleNotFoundException {
        UserServiceModel model = this.userService.register(this.testModel);

        assertEquals("User handle is not mapped correctly.", model.getHandle(), this.testModel.getHandle());
        assertEquals("User name is not mapped correctly.", model.getName(), this.testModel.getName());
        assertEquals("User email is not mapped correctly.", model.getEmail(), this.testModel.getEmail());
        assertEquals("User password is not mapped correctly.", model.getPassword(), HASHED_PASSWORD);
        assertTrue("User enabled is not mapped correctly.", model.isEnabled());
        assertTrue("User account non expired is not mapped correctly.", model.isAccountNonExpired());
        assertTrue("User account non locked is not mapped correctly.", model.isAccountNonLocked());
        assertTrue("User credentials non expired is not mapped correctly.", model.isCredentialsNonExpired());

        assertTrue("User has more or less than 1 role.", model.getAuthorities().size() == 1);
        assertTrue("User does not have user role.", model.getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_USER)));
    }
}
