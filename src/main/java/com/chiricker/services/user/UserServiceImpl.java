package com.chiricker.services.user;

import com.chiricker.models.binding.UserRegisterBindingModel;
import com.chiricker.models.entities.Role;
import com.chiricker.models.entities.User;
import com.chiricker.repositories.UserRepository;
import com.chiricker.services.role.RoleService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Component(value = "userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper mapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.initCustomMappings();
    }

    private void initCustomMappings() {
        Converter<UserRegisterBindingModel, User> register = new Converter<UserRegisterBindingModel, User>() {
            @Override
            public User convert(MappingContext<UserRegisterBindingModel, User> context) {
                UserRegisterBindingModel s = context.getSource();
                User d = context.getDestination();

                d.setName(s.getName());
                d.setHandle(s.getHandle());
                d.setEmail(s.getEmail());
                d.setAccountNonExpired(true);
                d.setAccountNonLocked(true);
                d.setCredentialsNonExpired(true);
                d.setEnabled(true);
                d.setPassword(passwordEncoder.encode(s.getPassword()));

                Role userRole = roleService.getUserRole();
                Set<Role> roles = new HashSet<>() {{
                    add(userRole);
                }};

                d.setAuthorities(roles);

                return d;
            }
        };

        this.mapper.addConverter(register);
    }

    @Override
    public boolean handleExists(String handle) {
        return this.userRepository.existsByHandleIs(handle);
    }

    @Override
    public User register(UserRegisterBindingModel model) {
        User user = this.mapper.map(model, User.class);
        this.userRepository.saveAndFlush(user);

        return user;
    }

    @Override
    public boolean handleAndPasswordMatch(String handle, String password) {
        String encryptedPass = this.passwordEncoder.encode(password);
        return this.userRepository.existsByHandleAndPassword(handle, encryptedPass);
    }

    @Override
    public UserDetails loadUserByUsername(String handle) throws UsernameNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UsernameNotFoundException("No user with handle: " + handle);

        return user;
    }
}
