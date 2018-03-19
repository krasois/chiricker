package com.chiricker.services.role;

import com.chiricker.models.entities.Role;
import com.chiricker.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private static final String USER_ROLE_NAME = "USER";

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getUserRole() {
        return this.roleRepository.findByName(USER_ROLE_NAME);
    }
}
