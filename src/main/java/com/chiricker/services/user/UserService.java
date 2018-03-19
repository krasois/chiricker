package com.chiricker.services.user;

import com.chiricker.models.binding.UserRegisterBindingModel;
import com.chiricker.models.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

    boolean handleExists(String handle);

    User register(UserRegisterBindingModel model);

    boolean handleAndPasswordMatch(String handle, String password);
}
