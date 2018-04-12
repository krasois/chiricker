package com.chiricker.users.services.user;

import com.chiricker.users.exceptions.UserNotFoundException;
import com.chiricker.users.models.binding.UserRegisterBindingModel;
import com.chiricker.users.models.entities.User;
import com.chiricker.users.models.binding.UserEditBindingModel;
import com.chiricker.users.models.view.ProfileViewModel;
import com.chiricker.users.models.view.UserCardViewModel;
import com.chiricker.users.models.view.UserNavbarViewModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

    User getByHandle(String handle);

    boolean handleExists(String handle);

    User register(UserRegisterBindingModel model);

    User edit(UserEditBindingModel model, String handle) throws UserNotFoundException;

    UserEditBindingModel getUserSettings(String handle);

    UserCardViewModel getUserCard(String handle);

    UserNavbarViewModel getNavbarInfo(String handle);

    ProfileViewModel getProfileByHandle(String handle) throws UserNotFoundException;
}
