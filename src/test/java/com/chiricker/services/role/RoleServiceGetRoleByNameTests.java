package com.chiricker.services.role;

import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.repositories.RoleRepository;
import com.chiricker.areas.users.services.role.RoleServiceImpl;
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
import org.springframework.test.context.ActiveProfiles;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RoleServiceGetRoleByNameTests {

    private static final Long USER_ROLE_ID = 1L;
    private static final String USER_ROLE_AUTHORITY = "ROLE_USER";
    private static final Long ADMIN_ROLE_ID = 2L;
    private static final String ADMIN_ROLE_AUTHORITY = "ROLE_ADMIN";

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Before
    public void setup() {
        Role userRole = new Role();
        userRole.setId(USER_ROLE_ID);
        userRole.setAuthority(USER_ROLE_AUTHORITY);

        Role adminRole = new Role();
        adminRole.setId(ADMIN_ROLE_ID);
        adminRole.setAuthority(ADMIN_ROLE_AUTHORITY);

        Mockito.when(this.roleRepository.findByAuthority(USER_ROLE_AUTHORITY)).thenReturn(userRole);
        Mockito.when(this.roleRepository.findByAuthority(ADMIN_ROLE_AUTHORITY)).thenReturn(adminRole);
        Mockito.when(this.mapper.map(ArgumentMatchers.any(), ArgumentMatchers.eq(RoleServiceModel.class))).thenAnswer(r -> {
            Role role = r.getArgument(0);
            RoleServiceModel model = new RoleServiceModel();
            model.setAuthority(role.getAuthority());
            model.setId(role.getId());
            return model;
        });
    }

    @Test
    public void testGetRoleByName_WithValidRoleName_ShouldNotReturnNull() {
        RoleServiceModel roleModel = this.roleService.getRoleByName("ROLE_USER");

        Assert.assertNotEquals("Role service model is null.", null, roleModel);
    }

    @Test
    public void testGetRoleByName_WithInvalidRoleName_ShouldReturnNull() {
        RoleServiceModel roleModel = this.roleService.getRoleByName("ROLE_MODERATOR");

        Assert.assertEquals("Role service model is not null.", null, roleModel);
    }

    @Test
    public void testGetRoleByName_WithValidRoleName_ShouldMapFieldsCorrectly() {
        RoleServiceModel roleModel = this.roleService.getRoleByName("ROLE_ADMIN");

        Assert.assertEquals("Role Id is not mapped correctly.", ADMIN_ROLE_ID, roleModel.getId());
        Assert.assertEquals("Role Authority is not mapped correctly.", ADMIN_ROLE_AUTHORITY, roleModel.getAuthority());
    }
}