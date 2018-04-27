package com.chiricker.areas.users.services.user;

import com.chiricker.areas.admin.models.binding.EditUserBindingModel;
import com.chiricker.areas.admin.models.view.UserPanelViewModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.models.service.UserServiceModelTP;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.exceptions.UserRoleNotFoundException;
import com.chiricker.areas.users.models.binding.FollowBindingModel;
import com.chiricker.areas.users.models.binding.UserRegisterBindingModel;
import com.chiricker.areas.users.models.binding.UserEditBindingModel;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.models.view.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public interface UserService extends UserDetailsService {

    UserServiceModel getByHandleModel(String handle);

    SimpleUserServiceModel getByHandleSimple(String handle);

    Set<TimelineUserServiceModel> getUserFollowerTimelineIds(String userId);

    String getIdForHandle(String handle);

    boolean handleExists(String handle);

    UserServiceModel register(UserRegisterBindingModel model) throws UserRoleNotFoundException;

    UserServiceModel edit(UserEditBindingModel model, String handle) throws UserNotFoundException;

    FollowResultViewModel follow(FollowBindingModel model, String requesterHandle) throws UserNotFoundException;

    UserEditBindingModel getUserSettings(String handle) throws UserNotFoundException;

    UserCardViewModel getUserCard(String handle) throws UserNotFoundException;

    UserNavbarViewModel getNavbarInfo(String handle) throws UserNotFoundException;

    ProfileViewModel getProfileByHandle(String handle, String requesterHandle) throws UserNotFoundException;

    Future updateUserProfilePicUrl(String userHandle, String pictureUrl) throws UserNotFoundException;

    List<FollowerViewModel> getFollowersForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<FollowerViewModel> getFollowingForUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    PeerSearchResultViewModel getPeers(String query, String requesterHandle, Pageable pageable) throws UserNotFoundException;



    Page<UserPanelViewModel> getEnabledUsersForAdmin(Pageable pageable);

    Page<UserPanelViewModel> getDisabledUsersForAdmin(Pageable pageable);

    EditUserBindingModel getUserSettingsAdmin(String id) throws UserNotFoundException;

    UserServiceModel editAdmin(String id, EditUserBindingModel user) throws UserNotFoundException, UserRoleNotFoundException;

    UserServiceModel disableUser(String id) throws UserNotFoundException;

    UserServiceModel enableUser(String id) throws UserNotFoundException;
}