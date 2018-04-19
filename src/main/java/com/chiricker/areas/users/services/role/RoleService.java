package com.chiricker.areas.users.services.role;

import com.chiricker.areas.users.models.service.RoleServiceModel;

public interface RoleService {

    RoleServiceModel getRoleByName(String name);

    RoleServiceModel createRole(String roleName);
}
