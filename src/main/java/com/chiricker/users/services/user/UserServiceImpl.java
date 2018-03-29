package com.chiricker.users.services.user;

import com.chiricker.users.models.binding.UserRegisterBindingModel;
import com.chiricker.users.models.entities.Profile;
import com.chiricker.users.models.entities.Role;
import com.chiricker.users.models.entities.User;
import com.chiricker.users.models.binding.UserEditBindingModel;
import com.chiricker.users.models.view.UserCardViewModel;
import com.chiricker.users.models.view.UserNavbarViewModel;
import com.chiricker.users.repositories.UserRepository;
import com.chiricker.users.services.role.RoleService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
                d.setRegisteredOn(new Date());
                Profile profile = new Profile() {{
                    setProfilePicUrl("https://upload.wikimedia.org/wikipedia/commons/7/70/User_icon_BLACK-01.png");
                }};
                d.setProfile(profile);

                Role userRole = roleService.getUserRole();
                Set<Role> roles = new HashSet<>() {{
                    add(userRole);
                }};

                d.setAuthorities(roles);

                return d;
            }
        };

        Converter<UserEditBindingModel, User> edit = new Converter<UserEditBindingModel, User>() {
            @Override
            public User convert(MappingContext<UserEditBindingModel, User> context) {
                UserEditBindingModel s = context.getSource();
                User d = context.getDestination();

                d.setName(s.getName());
                d.setHandle(s.getHandle());
                d.setEmail(s.getEmail());
                if (!s.getPassword().equals("")) d.setPassword(passwordEncoder.encode(s.getPassword()));
                d.getProfile().setBiography(s.getBiography());
                d.getProfile().setWebsiteUrl(s.getWebsiteUrl());

                return d;
            }
        };

        Converter<User, UserEditBindingModel> userSettings = new Converter<User, UserEditBindingModel>() {
            @Override
            public UserEditBindingModel convert(MappingContext<User, UserEditBindingModel> context) {
                User s = context.getSource();
                UserEditBindingModel d = context.getDestination();

                d.setName(s.getName());
                d.setHandle(s.getHandle());
                d.setEmail(s.getEmail());
                d.setBiography(s.getProfile().getBiography());
                d.setWebsiteUrl(s.getProfile().getWebsiteUrl());

                return d;
            }
        };

        Converter<User, UserCardViewModel> userCard = new Converter<User, UserCardViewModel>() {
            @Override
            public UserCardViewModel convert(MappingContext<User, UserCardViewModel> context) {
                User s = context.getSource();
                UserCardViewModel d = context.getDestination();

                d.setName(s.getName());
                d.setHandle(s.getHandle());
                d.setBiography(s.getProfile().getBiography());
                d.setWebsiteUrl(s.getProfile().getWebsiteUrl());
                d.setProfilePicUrl(s.getProfile().getProfilePicUrl());

                return d;
            }
        };

        this.mapper.addConverter(register);
        this.mapper.addConverter(edit);
        this.mapper.addConverter(userSettings);
        this.mapper.addConverter(userCard);
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
    public User edit(UserEditBindingModel model, String handle) {
        User user = this.userRepository.findByHandle(handle);
        this.mapper.map(model, user);
        this.userRepository.saveAndFlush(user);
        return user;
    }

    @Override
    public UserEditBindingModel getUserSettings(String handle) {
        User user = this.userRepository.findByHandle(handle);
        return this.mapper.map(user, UserEditBindingModel.class);
    }

    @Override
    public UserCardViewModel getUserCard(String handle) {
        User user = this.userRepository.findByHandle(handle);
        return this.mapper.map(user, UserCardViewModel.class);
    }

    @Override
    public UserNavbarViewModel getNavbarInfo(String handle) {
        User user = this.userRepository.findByHandle(handle);
        return this.mapper.map(user, UserNavbarViewModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String handle) throws UsernameNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UsernameNotFoundException("No user with handle: " + handle);
        return user;
    }
}
