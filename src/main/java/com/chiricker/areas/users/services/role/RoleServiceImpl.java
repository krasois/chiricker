package com.chiricker.areas.users.services.role;

import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.repositories.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper mapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @Override
    public RoleServiceModel getRoleByName(String name) {
        Role role = this.roleRepository.findByAuthority(name);
        if (role == null) return null;
        return this.mapper.map(role, RoleServiceModel.class);
    }

    @Override
    public RoleServiceModel createRole(String roleName) {
        Role role = new Role();
        role.setAuthority(roleName);
        role = this.roleRepository.saveAndFlush(role);
        return this.mapper.map(role, RoleServiceModel.class);
    }
}