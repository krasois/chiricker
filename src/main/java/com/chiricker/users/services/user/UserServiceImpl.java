package com.chiricker.users.services.user;

import com.chiricker.users.exceptions.UserNotFoundException;
import com.chiricker.users.models.binding.UserRegisterBindingModel;
import com.chiricker.users.models.entities.Profile;
import com.chiricker.users.models.entities.Role;
import com.chiricker.users.models.entities.User;
import com.chiricker.users.models.binding.UserEditBindingModel;
import com.chiricker.users.models.view.ProfileViewModel;
import com.chiricker.users.models.view.UserCardViewModel;
import com.chiricker.users.models.view.UserNavbarViewModel;
import com.chiricker.users.repositories.UserRepository;
import com.chiricker.users.services.role.RoleService;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Component(value = "userService")
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_PROFILE_PICTURE_URL = "https://dl.dropboxusercontent.com/s/y7i72l1yblmfydj/defaultProfileImage.png";

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    private final DbxClientV2 client;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper mapper, BCryptPasswordEncoder passwordEncoder, DbxClientV2 client) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.client = client;
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
                    setProfilePicUrl(DEFAULT_PROFILE_PICTURE_URL);
                }};
                d.setProfile(profile);

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

        Converter<User, ProfileViewModel> profile = new Converter<User, ProfileViewModel>() {
            @Override
            public ProfileViewModel convert(MappingContext<User, ProfileViewModel> context) {
                User s = context.getSource();
                ProfileViewModel d = context.getDestination();

                d.setHandle(s.getHandle());
                d.setName(s.getName());
                d.setBiography(s.getProfile().getBiography());
                d.setProfilePicUrl(s.getProfile().getProfilePicUrl());
                d.setWebsiteUrl(s.getProfile().getWebsiteUrl());
                d.setChiricksCount(s.getChiricks().size());
                d.setFollowersCount(0);
                d.setFollowingCount(0);

                return d;
            }
        };

        this.mapper.addConverter(register);
        this.mapper.addConverter(edit);
        this.mapper.addConverter(userSettings);
        this.mapper.addConverter(userCard);
        this.mapper.addConverter(profile);
    }

    @Async
    public void handlePictureFile(User user, MultipartFile profilePicture) {
        if (profilePicture.getSize() > 0) {
            String oldProfilePic = user.getProfile().getProfilePicUrl();

            try (InputStream in = profilePicture.getInputStream()) {

                String fileName = profilePicture.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                String newFileRandomName = "/" + UUID.randomUUID().toString() + fileExtension;

                FileMetadata fileMetadata = this.client.files()
                        .uploadBuilder(newFileRandomName)
                        .uploadAndFinish(in);

                SharedLinkMetadata slm = this.client.sharing()
                        .createSharedLinkWithSettings(newFileRandomName,
                                SharedLinkSettings.newBuilder().withRequestedVisibility(RequestedVisibility.PUBLIC).build());

                String imageShareUrl = slm.getUrl()
                        .replace("www.dropbox.com", "dl.dropboxusercontent.com")
                        .replace("?dl=0", "");

                user.getProfile().setProfilePicUrl(imageShareUrl);
            } catch (IOException | DbxException e) {
                e.printStackTrace();
            }


            if (!oldProfilePic.equals(DEFAULT_PROFILE_PICTURE_URL)) {
                String filePath = oldProfilePic.substring(oldProfilePic.lastIndexOf("/"));
                try {
                    this.client.files().deleteV2(filePath);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public User getByHandle(String handle) {
        return this.userRepository.findByHandle(handle);
    }

    @Override
    public boolean handleExists(String handle) {
        return this.userRepository.existsByHandleIs(handle);
    }

    @Override
    public User register(UserRegisterBindingModel model) {
        User user = this.mapper.map(model, User.class);
        Role userRole = roleService.getUserRole();

        Set<Role> roles = new HashSet<>() {{
            add(userRole);
        }};
        user.setAuthorities(roles);

        return this.userRepository.saveAndFlush(user);
    }

    @Override
    public User edit(UserEditBindingModel model, String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException("User with handle " + handle + " was not found");

        this.mapper.map(model, user);
        this.handlePictureFile(user, model.getProfilePicture());

        return this.userRepository.saveAndFlush(user);
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
    public ProfileViewModel getProfileByHandle(String handle) throws UserNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UserNotFoundException("User with handle " + handle + " was not found");
        return this.mapper.map(user, ProfileViewModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String handle) throws UsernameNotFoundException {
        User user = this.userRepository.findByHandle(handle);
        if (user == null) throw new UsernameNotFoundException("No user with handle: " + handle);
        return user;
    }
}