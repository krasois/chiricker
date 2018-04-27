package com.chiricker.areas.users.services.user;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.admin.models.view.UserPanelViewModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.models.service.UserServiceModelTP;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.entities.Role;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.service.RoleServiceModel;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.models.view.*;
import com.chiricker.areas.users.repositories.UserRepository;
import com.chiricker.areas.users.services.role.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Future;
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
    public UserServiceModel getByHandleModel(String handle) {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) return null;
        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public SimpleUserServiceModel getByHandleSimple(String handle) {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) return null;
        return this.mapper.map(user, SimpleUserServiceModel.class);
    }

    @Override
    public Set<TimelineUserServiceModel> getUserFollowerTimelineIds(String userId) {
        User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) return null;

        return user.getFollowers().stream()
                .map(f -> {
                    TimelineUserServiceModel result = new TimelineUserServiceModel();
                    result.setId(f.getTimeline().getId());
                    result.setUserId(f.getId());
                    return result;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public String getIdForHandle(String handle) {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) return null;
        return user.getId();
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

        user.setPassword(passwordEncoder.encode(model.getPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        user = this.userRepository.save(user);
        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public UserServiceModel edit(UserEditBindingModel model, String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException("User with handle " + handle + " was not found");

        user.setName(model.getName());
        user.setHandle(model.getHandle());
        user.setEmail(model.getEmail());
        user.getProfile().setBiography(model.getBiography());
        user.getProfile().setWebsiteUrl(model.getWebsiteUrl());
        if (!model.getPassword().equals("")) {
            if (this.passwordEncoder.matches(user.getPassword(), model.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(model.getName()));
            }
        }

        this.userRepository.save(user);

        return this.mapper.map(user, UserServiceModel.class);
    }

    @Override
    public FollowResultViewModel follow(FollowBindingModel model, String requesterHandle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(model.getHandle());
        if (user == null) throw new UserNotFoundException("User " + model.getHandle() + " was not found");

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException("User " + requesterHandle + " was not found");

        if (requester == user) throw new IllegalStateException("Cannot follow yourself");

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
    public UserEditBindingModel getUserSettings(String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException();

        UserEditBindingModel userModel = new UserEditBindingModel();
        userModel.setName(user.getName());
        userModel.setHandle(user.getHandle());
        userModel.setEmail(user.getEmail());
        userModel.setBiography(user.getProfile().getBiography());
        userModel.setWebsiteUrl(user.getProfile().getWebsiteUrl());
        userModel.setPictureUrl(user.getProfile().getProfilePicUrl());
        return userModel;
    }

    @Override
    public UserCardViewModel getUserCard(String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException();

        UserCardViewModel cardViewModel = new UserCardViewModel();
        cardViewModel.setName(user.getName());
        cardViewModel.setHandle(user.getHandle());
        cardViewModel.setBiography(user.getProfile().getBiography());
        cardViewModel.setWebsiteUrl(user.getProfile().getWebsiteUrl());
        cardViewModel.setProfilePicUrl(user.getProfile().getProfilePicUrl());
        return cardViewModel;
    }

    @Override
    public UserNavbarViewModel getNavbarInfo(String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException();
        UserNavbarViewModel viewModel = this.mapper.map(user, UserNavbarViewModel.class);
        long notificationsCount = user.getNotifications()
                .stream()
                .filter(n -> !n.isChecked())
                .count();
        viewModel.setNotificationsCount(notificationsCount);
        return viewModel;
    }

    @Override
    public ProfileViewModel getProfileByHandle(String handle, String requesterHandle) throws UserNotFoundException {
        User user = this.userRepository.findByIsEnabledIsTrueAndHandle(handle);
        if (user == null) throw new UserNotFoundException();

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException();

        ProfileViewModel profileViewModel = new ProfileViewModel();
        profileViewModel.setHandle(user.getHandle());
        profileViewModel.setName(user.getName());
        profileViewModel.setProfileBiography(user.getProfile().getBiography());
        profileViewModel.setProfilePicUrl(user.getProfile().getProfilePicUrl());
        profileViewModel.setProfileWebsiteUrl(user.getProfile().getWebsiteUrl());
        profileViewModel.setChiricksCount(user.getChiricks().size());
        profileViewModel.setFollowersCount(user.getFollowers().size());
        profileViewModel.setFollowingCount(user.getFollowing().size());

        boolean isFollowing = requester.getFollowing().contains(user);
        profileViewModel.setFollowing(isFollowing);

        return profileViewModel;
    }

    @Override
    public List<FollowerViewModel> getFollowersForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException();

        List<User> followers = this.userRepository.findAllByFollowingContainingOrderByHandle(user, pageable);

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

        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException();

        List<User> followers = this.userRepository.findAllByFollowersContainingOrderByHandle(user, pageable);

        List<FollowerViewModel> followerModels = new ArrayList<>(followers.size());
        for (User follower : followers) {
            FollowerViewModel followerModel = this.mapFollowerViewModel(follower, requester);
            followerModels.add(followerModel);
        }

        return followerModels;
    }

    @Async
    @Override
    @Transactional
    public Future updateUserProfilePicUrl(String userHandle, String pictureUrl) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");

        user.getProfile().setProfilePicUrl(pictureUrl);
        this.userRepository.save(user);

        UserServiceModel result = this.mapper.map(user, UserServiceModel.class);
        return new AsyncResult<>(result);
    }

    @Override
    public PeerSearchResultViewModel getPeers(String query, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User requester = this.userRepository.findByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException();

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

        user.setName(model.getName());
        user.setHandle(model.getHandle());
        user.setEmail(model.getEmail());
        user.getProfile().setBiography(model.getProfileBiography());
        user.getProfile().setWebsiteUrl(model.getProfileWebsiteUrl());
        if (!model.getPassword().equals("")) {
            if (this.passwordEncoder.matches(user.getPassword(), model.getPassword())) {
                user.setPassword(this.passwordEncoder.encode(model.getName()));
            }
        }

        Set<Role> authorities = new HashSet<>();
        boolean hasGodRole = user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_GOD"));
        if (hasGodRole) {
            Role godRole = this.mapRoleModelToRole("ROLE_GOD");
            authorities.add(godRole);
        }

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

    @PostConstruct
    public void createRolesAndGodUser() {
        String godName = "f6d67eb9_16ad_4311_958b_370a5b204a25";
        String godPass = "f6d67eb9_16ad_4311_958b_";
        String userRoleName = "ROLE_USER";
        String adminRoleName = "ROLE_ADMIN";
        String godRoleName = "ROLE_GOD";
        User godUser = this.userRepository.findByHandle(godName);
        if (godUser != null) return;

        Role userRole;
        Role adminRole;
        Role godRole;
        RoleServiceModel userRoleModel = this.roleService.getRoleByName(userRoleName);
        RoleServiceModel adminRoleModel = this.roleService.getRoleByName(adminRoleName);
        RoleServiceModel godRoleModel = this.roleService.getRoleByName(godRoleName);

        if (userRoleModel == null) userRoleModel = this.roleService.createRole(userRoleName);
        userRole = this.mapper.map(userRoleModel, Role.class);

        if (adminRoleModel == null) adminRoleModel = this.roleService.createRole(adminRoleName);
        adminRole = this.mapper.map(adminRoleModel, Role.class);

        if (godRoleModel == null) godRoleModel = this.roleService.createRole(godRoleName);
        godRole = this.mapper.map(godRoleModel, Role.class);

        Set<Role> roles = new HashSet<>(Arrays.asList(userRole, adminRole, godRole));
        godUser = new User();
        godUser.setHandle(godName);
        godUser.setName("GOD");
        godUser.setPassword(this.passwordEncoder.encode(godPass));
        godUser.setEmail("god@god.god");
        godUser.setAuthorities(roles);
        godUser.setAccountNonExpired(true);
        godUser.setAccountNonLocked(true);
        godUser.setEnabled(true);
        godUser.setCredentialsNonExpired(true);

        this.userRepository.save(godUser);
    }
}