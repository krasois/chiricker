package com.chiricker.areas.users.services.role;

import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.repositories.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private static final String USER_ROLE_AUTHORITY = "ROLE_USER";

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getUserRole() {
        Role role = this.roleRepository.findByAuthority(USER_ROLE_AUTHORITY);
        if (role == null) return null;
        return role;
    }
}