package com.chiricker.areas.chiricks.services.chirick;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.areas.chiricks.models.binding.CommentBindingModel;
import com.chiricker.areas.chiricks.models.binding.LikeBindingModel;
import com.chiricker.areas.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.view.*;
import com.chiricker.areas.chiricks.repositories.ChirickRepository;
import com.chiricker.areas.chiricks.enums.ActivityType;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.services.notification.NotificationService;
import com.chiricker.areas.users.services.user.UserService;
import com.chiricker.util.linker.UserLinker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChirickServiceImpl implements ChirickService {

    private final ChirickRepository chirickRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ModelMapper mapper;

    @Autowired
    public ChirickServiceImpl(ChirickRepository chirickRepository, UserService userService, NotificationService notificationService, ModelMapper mapper) {
        this.chirickRepository = chirickRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    private ChirickViewModel createChirickViewModel(Chirick chirick, String userId) {
        ChirickViewModel chirickModel = this.mapper.map(chirick, ChirickViewModel.class);

        String chirickContent = UserLinker.linkUsers(chirick.getChirick());
        chirickModel.setChirick(chirickContent);

        chirickModel.setRechiricksSize(chirick.getRechiricks().size());
        chirickModel.setLikesSize(chirick.getLikes().size());
        chirickModel.setCommentsSize(chirick.getComments().size());

        chirickModel.setRechiricked(chirick.getRechiricks().stream()
                .anyMatch(u -> u.getId().equals(userId)));

        chirickModel.setLiked(chirick.getLikes().stream()
                .anyMatch(u -> u.getId().equals(userId)));

        chirickModel.setCommented(chirick.getComments().stream()
                .anyMatch(u -> u.getUser().getId().equals(userId)));

        if (chirick.getParent() != null) {
            Chirick parent = chirick.getParent();
            String parentUrl = "/@" + parent.getUser().getHandle() + "/" + parent.getId();
            chirickModel.setParentUrl(parentUrl);
        }

        return chirickModel;
    }

    private User getUserWithHandle(String handle) throws UserNotFoundException {
        User user = this.userService.getByHandle(handle);
        if (user == null) throw new UserNotFoundException();
        return user;
    }

    private List<ChirickViewModel> getActivityFromUser(String userHandle, String requesterHandle, Pageable pageable, ActivityType type) throws UserNotFoundException {
        User requester = this.getUserWithHandle(requesterHandle);
        User user = this.getUserWithHandle(userHandle);
        if (user == null) return new ArrayList<>();

        List<Chirick> chiricks;
        if (type == ActivityType.CHIRICKS) {
            chiricks = this.chirickRepository.findAllByUserHandleAndParentIsNullOrderByDateDesc(userHandle, pageable);
        } else if (type == ActivityType.COMMENTS) {
            chiricks = this.chirickRepository.findAllByUserHandleAndParentIsNotNullOrderByDateDesc(userHandle, pageable);
        } else {
            Set<Chirick> collection = type == ActivityType.RECHIRICKS ? user.getRechiricks() : user.getLikes();
            if (collection.size() < 1) return new ArrayList<>();
            chiricks = this.chirickRepository.getChiricksInCollection(collection, pageable);
        }

        List<ChirickViewModel> chirickModels = new ArrayList<>(chiricks.size());
        for (Chirick chirick : chiricks) {
            chirickModels.add(this.createChirickViewModel(chirick, requester.getId()));
        }

        return chirickModels;
    }

    @Override
    public ChirickServiceModel getById(String chirickId) {
        Chirick chirick = this.chirickRepository.findById(chirickId).orElse(null);
        if (chirick == null) return null;
        return this.mapper.map(chirick, ChirickServiceModel.class);
    }

    @Override
    public ChirickServiceModel add(ChirickBindingModel bindingModel, String handle) throws UserNotFoundException {
        User user = this.getUserWithHandle(handle);
        if (user == null) throw new UserNotFoundException();

        Chirick chirick = this.mapper.map(bindingModel, Chirick.class);
        String escapedChirick = chirick.getChirick();
        chirick.setChirick(escapedChirick);
        chirick.setUser(user);
        chirick = this.chirickRepository.save(chirick);

        this.notificationService.notifyUsers(chirick);

        return this.mapper.map(chirick, ChirickServiceModel.class);
    }

    @Override
    public ChirickCommentResultViewModel comment(CommentBindingModel model, String handle) throws ChirickNotFoundException, UserNotFoundException {
        User user = this.getUserWithHandle(handle);
        if (user == null) throw new UserNotFoundException();

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null)
            throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        ChirickCommentResultViewModel result = new ChirickCommentResultViewModel();

        Chirick comment = new Chirick();
        comment.setChirick(model.getComment());
        comment.setParent(chirick);
        comment.setUser(user);

        Set<Chirick> chirickComments = chirick.getComments();
        chirickComments.add(comment);
        chirick.setComments(chirickComments);
        chirick = this.chirickRepository.save(chirick);

        this.notificationService.notifyUsers(comment);

        result.setId(chirick.getId());
        result.setCommentsSize(chirickComments.size());
        return result;
    }

    @Override
    public RechirickResultViewModel rechirick(RechirickBindingModel model, String handle) throws ChirickNotFoundException, UserNotFoundException {
        User user = this.getUserWithHandle(handle);

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null)
            throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        RechirickResultViewModel result = new RechirickResultViewModel();

        Set<User> usersRechiricked = chirick.getRechiricks();
        User userRechiricked = usersRechiricked.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        if (userRechiricked != null) {
            usersRechiricked.remove(userRechiricked);
        } else {
            usersRechiricked.add(user);
            result.setRechiricked(true);
        }

        chirick.setRechiricks(usersRechiricked);
        this.chirickRepository.save(chirick);

        result.setId(chirick.getId());
        result.setRechiricksSize(usersRechiricked.size());
        return result;
    }

    @Override
    public ChirickLikeResultViewModel like(LikeBindingModel model, String handle) throws ChirickNotFoundException, UserNotFoundException {
        User user = this.getUserWithHandle(handle);
        if (user == null) return null;

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null)
            throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        ChirickLikeResultViewModel result = new ChirickLikeResultViewModel();

        Set<User> usersLiked = chirick.getLikes();
        User userLiked = usersLiked.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        if (userLiked != null) {
            usersLiked.remove(userLiked);
        } else {
            usersLiked.add(user);
            result.setLiked(true);
        }

        chirick.setLikes(usersLiked);
        this.chirickRepository.save(chirick);

        result.setId(chirick.getId());
        result.setLikesSize(usersLiked.size());
        return result;
    }

    @Override
    public List<ChirickViewModel> getChiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        return this.getActivityFromUser(userHandle, requesterHandle, pageable, ActivityType.CHIRICKS);
    }

    @Override
    public List<ChirickViewModel> getCommentsFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        return this.getActivityFromUser(userHandle, requesterHandle, pageable, ActivityType.COMMENTS);
    }

    @Override
    public List<ChirickViewModel> getRechiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        return this.getActivityFromUser(userHandle, requesterHandle, pageable, ActivityType.RECHIRICKS);
    }

    @Override
    public List<ChirickViewModel> getLikesFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        return this.getActivityFromUser(userHandle, requesterHandle, pageable, ActivityType.LIKES);
    }

    @Override
    public List<ChirickViewModel> getCommentsFromChirick(String postId, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User requester = this.getUserWithHandle(requesterHandle);

        List<Chirick> comments = this.chirickRepository.findAllByParentIdOrderByDateDesc(postId, pageable);
        if (comments.size() < 1) return new ArrayList<>();

        List<ChirickViewModel> chirickModels = new ArrayList<>(comments.size());
        for (Chirick comment : comments) {
            chirickModels.add(this.createChirickViewModel(comment, requester.getId()));
        }

        return chirickModels;
    }

    @Override
    public ChirickDetailsViewModel getChirickDetails(String chirickId, String requesterHandle) throws ChirickNotFoundException, UserNotFoundException {
        Chirick chirick = this.chirickRepository.findById(chirickId).orElse(null);
        if (chirick == null) throw new ChirickNotFoundException("Chirick is nowhere to be found");

        User requester = this.getUserWithHandle(requesterHandle);

        ChirickDetailsViewModel details = new ChirickDetailsViewModel();

        ChirickViewModel chirickModel = this.createChirickViewModel(chirick, requester.getId());

        ChirickUserViewModel userDetails = this.mapper.map(chirick.getUser(), ChirickUserViewModel.class);
        boolean isFollowing = chirick.getUser().getFollowers().contains(requester);
        userDetails.setIsFollowing(isFollowing);

        details.setChirick(chirickModel);
        details.setUser(userDetails);
        details.setDate(chirick.getDate());

        return details;
    }
}