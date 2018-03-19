package com.chiricker.services.user;

import com.chiricker.models.binding.UserRegisterBindingModel;
import com.chiricker.models.entities.User;

public interface UserService {

    boolean handleExists(String handle);

    User register(UserRegisterBindingModel model);

    boolean handleAndPasswordMatch(String handle, String password);
}
