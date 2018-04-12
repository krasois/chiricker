package com.chiricker.users.services.role;

import com.chiricker.users.models.entities.Role;
import com.chiricker.users.repositories.RoleRepository;
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
        return this.roleRepository.findByAuthority(USER_ROLE_AUTHORITY);
    }
}
