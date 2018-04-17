package com.chiricker.areas.users.services.user;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.admin.models.view.UserPanelViewModel;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.models.view.*;
import com.chiricker.areas.users.repositories.UserRepository;
import com.chiricker.areas.users.services.role.RoleService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Component(value = "userService")
public class UserServiceImpl implements UserService {

    private static final String USER_ROLE_AUTHORITY = "ROLE_USER";

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

                d.setName(HtmlUtils
                        .htmlEscape(s.getName()));
                d.setHandle(HtmlUtils
                        .htmlEscape(s.getHandle()));
                d.setEmail(HtmlUtils
                        .htmlEscape(s.getEmail()));
                d.setAccountNonExpired(true);
                d.setAccountNonLocked(true);
                d.setCredentialsNonExpired(true);
                d.setEnabled(true);
                d.setPassword(passwordEncoder.encode(s.getPassword()));

                return d;
            }
        };

        Converter<UserEditBindingModel, User> edit = new Converter<UserEditBindingModel, User>() {
            @Override
            public User convert(MappingContext<UserEditBindingModel, User> context) {
                UserEditBindingModel s = context.getSource();
                User d = context.getDestination();

                d.setName(HtmlUtils
                        .htmlEscape(s.getName()));
                d.setHandle(HtmlUtils
                        .htmlEscape(s.getHandle()));
                d.setEmail(HtmlUtils
                        .htmlEscape(s.getEmail()));
                if (!s.getPassword().equals("")) d.setPassword(passwordEncoder.encode(s.getPassword()));
                d.getProfile().setBiography(HtmlUtils.htmlEscape(s.getBiography()));
                d.getProfile().setWebsiteUrl(HtmlUtils.htmlEscape(s.getWebsiteUrl()));

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
                d.setPictureUrl(s.getProfile().getProfilePicUrl());

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

        Converter<User, ProfileViewModel> profile = new Converter<User, ProfileViewModel>() {
            @Override
            public ProfileViewModel convert(MappingContext<User, ProfileViewModel> context) {
                User s = context.getSource();
                ProfileViewModel d = context.getDestination();

                d.setHandle(s.getHandle());
                d.setName(s.getName());
                d.setProfileBiography(s.getProfile().getBiography());
                d.setProfilePicUrl(s.getProfile().getProfilePicUrl());
                d.setProfileWebsiteUrl(s.getProfile().getWebsiteUrl());
                d.setChiricksCount(s.getChiricks().size());
                d.setFollowersCount(s.getFollowers().size());
                d.setFollowingCount(s.getFollowing().size());

                return d;
            }
        };

        this.mapper.addConverter(register);
        this.mapper.addConverter(edit);
        this.mapper.addConverter(userSettings);
        this.mapper.addConverter(userCard);
        this.mapper.addConverter(profile);
    }

    private FollowerViewModel mapFollowerViewModel(User follower, User requester) {
        FollowerViewModel followerModel = this.mapper.map(follower, FollowerViewModel.class);
        followerModel.setSelf(follower == requester);

        boolean isFollowed = requester.getFollowing().contains(follower);
        followerModel.setFollowed(isFollowed);

        return followerModel;
    }

    private UserServiceModel disableOrEnableUserById(String id, boolean enableValue) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException();

        user.setEnabled(enableValue);
        this.userRepository.save(user);

        return this.mapper.map(user, UserServiceModel.class);
    }

    private UserPanelViewModel mapForPanel(User user) {
        return this.mapper.map(user, UserPanelViewModel.class);
    }

    private Role mapRoleModelToRole(String role) throws UserRoleNotFoundException {
        RoleServiceModel userRoleModel = this.roleService.getRoleByName(role);
        if (userRoleModel == null) throw new UserRoleNotFoundException();
        return this.mapper.map(userRoleModel, Role.class);
    }

    @Override
    public UserServiceModel getByHandle(String handle) {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) return null;
        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public boolean handleExists(String handle) {
        return this.userRepository.existsByHandleIs(handle);
    }

    @Override
    public UserServiceModel register(UserRegisterBindingModel model) throws UserRoleNotFoundException {
        User user = this.mapper.map(model, User.class);
        Role userRole = mapRoleModelToRole(USER_ROLE_AUTHORITY);

        Set<Role> roles = new HashSet<>() {{
            add(userRole);
        }};
        user.setAuthorities(roles);

        this.userRepository.save(user);
        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserServiceModel edit(UserEditBindingModel model, String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException("User with handle " + handle + " was not found");

        this.mapper.map(model, user);
        this.userRepository.save(user);

        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public FollowResultViewModel follow(FollowBindingModel model, String requesterHandle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(model.getHandle());
        if (user == null) throw new UserNotFoundException("User " + model.getHandle() + "was not found");

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException("User " + requesterHandle + "was not found");

        if (requester == user) throw new IllegalStateException("User cannot follow themselves");

        FollowResultViewModel result = new FollowResultViewModel();
        Set<User> following = requester.getFollowing();
        if (following.contains(user)) {
            following.remove(user);
            result.setUnfollowed(true);
        } else {
            following.add(user);
        }

        this.userRepository.save(requester);

        return result;
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
    public ProfileViewModel getProfileByHandle(String handle, String requesterHandle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException("User with handle " + handle + " was not found");

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null)
            throw new UserNotFoundException("User with handle " + requesterHandle + " was not found");

        ProfileViewModel profileViewModel = this.mapper.map(user, ProfileViewModel.class);
        boolean isFollowing = requester.getFollowing().contains(user);
        profileViewModel.setFollowing(isFollowing);

        return profileViewModel;
    }

    @Override
    public List<FollowerViewModel> getFollowersForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");
        List<User> followers = this.userRepository.findAllByFollowingContainingOrderByHandle(user, pageable);

        User requester = this.userRepository.findByHandle(requesterHandle);
        List<FollowerViewModel> followerModels = new ArrayList<>(followers.size());
        for (User follower : followers) {
            FollowerViewModel followerModel = this.mapFollowerViewModel(follower, requester);
            followerModels.add(followerModel);
        }

        return followerModels;
    }

    @Override
    public List<FollowerViewModel> getFollowingForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");
        List<User> followers = this.userRepository.findAllByFollowersContainingOrderByHandle(user, pageable);

        User requester = this.userRepository.findByHandle(requesterHandle);
        List<FollowerViewModel> followerModels = new ArrayList<>(followers.size());
        for (User follower : followers) {
            FollowerViewModel followerModel = this.mapFollowerViewModel(follower, requester);
            followerModels.add(followerModel);
        }

        return followerModels;
    }

    @Override
    public UserServiceModel updateUserProfilePicUrl(String userHandle, String pictureUrl) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");

        user.getProfile().setProfilePicUrl(pictureUrl);
        this.userRepository.save(user);

        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public PeerSearchResultViewModel getPeers(String query, String requesterHandle, Pageable pageable) {
        User requester = this.userRepository.findByHandle(requesterHandle);
        Page<User> users = this.userRepository.findAllByNameContainingOrderByHandle(query, pageable);
        Page<FollowerViewModel> peers = users.map(u -> mapFollowerViewModel(u, requester));

        PeerSearchResultViewModel result = new PeerSearchResultViewModel();
        result.setPeers(peers);
        result.setQuery(query);

        return result;
    }

    @Override
    public Page<UserPanelViewModel> getEnabledUsersForAdmin(Pageable pageable) {
        Page<User> users = this.userRepository.findAllByIsEnabledTrue(pageable);
        return users.map(this::mapForPanel);
    }

    @Override
    public Page<UserPanelViewModel> getDisabledUsersForAdmin(Pageable pageable) {
        Page<User> users = this.userRepository.findAllByIsEnabledFalse(pageable);
        return users.map(this::mapForPanel);
    }

    @Override
    public EditUserBindingModel getUserSettingsAdmin(String id) throws UserNotFoundException {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException();

        EditUserBindingModel userModel = this.mapper.map(user, EditUserBindingModel.class);
        userModel.setAuthorities(user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));

        return userModel;
    }

    @Override
    public UserServiceModel editAdmin(String id, EditUserBindingModel model) throws UserNotFoundException, UserRoleNotFoundException {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException();

        user.setName(HtmlUtils
                .htmlEscape(model.getName()));
        user.setHandle(HtmlUtils
                .htmlEscape(model.getHandle()));
        user.setEmail(HtmlUtils
                .htmlEscape(model.getEmail()));
        if (!model.getPassword().equals("")) user.setName(this.passwordEncoder.encode(model.getName()));

        String escapedBio = HtmlUtils.htmlEscape(model.getProfileBiography());
        String escapedWebsiteUlr = HtmlUtils.htmlEscape(model.getProfileWebsiteUrl());
        user.getProfile().setBiography(escapedBio);
        user.getProfile().setWebsiteUrl(escapedWebsiteUlr);

        Set<Role> authorities = new HashSet<>();
        for (String role : model.getAuthorities()) {
            Role authority = this.mapRoleModelToRole(role);
            authorities.add(authority);
        }

        user.setAuthorities(authorities);
        this.userRepository.save(user);

        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserServiceModel disableUser(String id) throws UserNotFoundException {
        return this.disableOrEnableUserById(id, false);
    }

    @Override
    public UserServiceModel enableUser(String id) throws UserNotFoundException {
        return this.disableOrEnableUserById(id, true);
    }

    @Override
    public UserDetails loadUserByUsername(String handle) throws UsernameNotFoundException {
        User user = this.userRepository.findByIsEnabledIsTrueAndHandle(handle);
        if (user == null) throw new UsernameNotFoundException("No user with handle: " + handle);
        return user;
    }
}