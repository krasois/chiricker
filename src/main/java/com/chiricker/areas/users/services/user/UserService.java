package com.chiricker.areas.users.services.user;

import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.models.view.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService{

    User getByHandle(String handle);

    boolean handleExists(String handle);

    UserServiceModel register(UserRegisterBindingModel model) throws UserRoleNotFoundException;

    UserServiceModel edit(UserEditBindingModel model, String handle) throws UserNotFoundException;

    FollowResultViewModel follow(FollowBindingModel model, String requesterHandle) throws UserNotFoundException;

    UserEditBindingModel getUserSettings(String handle);

    UserCardViewModel getUserCard(String handle);

    UserNavbarViewModel getNavbarInfo(String handle);

    ProfileViewModel getProfileByHandle(String handle, String requesterHandle) throws UserNotFoundException;

    UserServiceModel updateUserProfilePicUrl(String userHandle, String pictureUrl) throws UserNotFoundException;

    List<FollowerViewModel> getFollowersForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<FollowerViewModel> getFollowingForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;
}
