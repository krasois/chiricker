package com.chiricker.chiricks.services.chirick;

import com.chiricker.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.chiricks.models.binding.ChirickCommentBindingModel;
import com.chiricker.chiricks.models.binding.ChirickLikeBindingModel;
import com.chiricker.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.chiricks.models.entities.Chirick;
import com.chiricker.chiricks.models.view.*;
import com.chiricker.chiricks.repositories.ChirickRepository;
import com.chiricker.users.exceptions.UserNotFoundException;
import com.chiricker.users.models.entities.User;
import com.chiricker.users.services.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChirickServiceImpl implements ChirickService {

    private final ChirickRepository chirickRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    @Autowired
    public ChirickServiceImpl(ChirickRepository chirickRepository, UserService userService, ModelMapper mapper) {
        this.chirickRepository = chirickRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    private ChirickViewModel mapActionPropertiesForChirick(Chirick chirick, User requester) {
            ChirickViewModel chirickModel = this.mapper.map(chirick, ChirickViewModel.class);
            chirickModel.setRechiricksSize(chirick.getRechiricks().size());
            chirickModel.setRechiricked(chirick.getRechiricks().contains(requester));

            chirickModel.setLikesSize(chirick.getLikes().size());
            chirickModel.setLiked(chirick.getLikes().contains(requester));

            chirickModel.setCommentsSize(chirick.getComments().size());
            chirickModel.setCommented(chirick.getComments().stream()
                    .anyMatch(c -> c.getUser() == requester));

            if (chirick.getParent() != null) {
                Chirick parent = chirick.getParent();
                String parentUrl = "/@" + parent.getUser().getHandle() + "/" + parent.getId();
                chirickModel.setParentUrl(parentUrl);
            }

        return chirickModel;
    }

    @Override
    public Chirick add(ChirickBindingModel bindingModel, String handle) throws UserNotFoundException {
        Chirick chirick = this.mapper.map(bindingModel, Chirick.class);
        User user = this.userService.getByHandle(handle);
        if (user == null) throw new UserNotFoundException("No user with " + handle + " was found");

        chirick.setUser(user);
        return this.chirickRepository.save(chirick);
    }

    @Override
    public RechirickResultViewModel rechirick(RechirickBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException {
        User user = this.userService.getByHandle(handle);
        if (user == null) throw new UserNotFoundException("No user with handle '" + handle + "' was found");

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null) throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        RechirickResultViewModel result = new RechirickResultViewModel();

        Set<User> usersRechiricked = chirick.getRechiricks();
        if (usersRechiricked.contains(user)) {
            usersRechiricked.remove(user);
        } else {
            usersRechiricked.add(user);
            result.setRechiricked(true);
        }

        chirick.setRechiricks(usersRechiricked);
        this.chirickRepository.save(chirick);

        result.setRechiricksSize(usersRechiricked.size());

        return result;
    }

    @Override
    public ChirickLikeResultViewModel like(ChirickLikeBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException {
        User user = this.userService.getByHandle(handle);
        if (user == null) throw new UserNotFoundException("No user with handle '" + handle + "' was found");

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null) throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        ChirickLikeResultViewModel result = new ChirickLikeResultViewModel();

        Set<User> usersLiked = chirick.getLikes();
        if (usersLiked.contains(user)) {
            usersLiked.remove(user);
        } else {
            usersLiked.add(user);
            result.setLiked(true);
        }

        chirick.setLikes(usersLiked);
        this.chirickRepository.save(chirick);

        result.setLikesSize(usersLiked.size());

        return result;
    }

    @Override
    public ChirickCommentResultViewModel comment(ChirickCommentBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException {
        User user = this.userService.getByHandle(handle);
        if (user == null) throw new UserNotFoundException("No user with handle '" + handle + "' was found");

        Chirick chirick = this.chirickRepository.findById(model.getId()).orElse(null);
        if (chirick == null) throw new ChirickNotFoundException("Chirick with id '" + model.getId() + "' was not found");

        ChirickCommentResultViewModel result = new ChirickCommentResultViewModel();

        Chirick comment = new Chirick();
        comment.setChirick(model.getComment());
        comment.setParent(chirick);
        comment.setUser(user);

        Set<Chirick> chirickComments = chirick.getComments();
        chirickComments.add(comment);
        chirick.setComments(chirickComments);

        this.chirickRepository.save(chirick);

        result.setCommentsSize(chirickComments.size());

        return result;
    }

    @Override
    public List<ChirickViewModel> getChiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) {
        User requester = this.userService.getByHandle(requesterHandle);

        List<Chirick> chiricks = this.chirickRepository.getChiricksByUser(userHandle, pageable);

        List<ChirickViewModel> chirickModels = new ArrayList<>(chiricks.size());
        for (Chirick chirick : chiricks) {
            chirickModels.add(this.mapActionPropertiesForChirick(chirick, requester));
        }

        return chirickModels;
    }

    @Override
    public List<ChirickViewModel> getCommentsFromUser(String userHandle, String requesterHandle, Pageable pageable) {
        User requester = this.userService.getByHandle(requesterHandle);

        List<Chirick> comments = this.chirickRepository.getCommentsByUser(userHandle, pageable);

        List<ChirickViewModel> chirickModels = new ArrayList<>(comments.size());
        for (Chirick chirick : comments) {
            chirickModels.add(this.mapActionPropertiesForChirick(chirick, requester));
        }

        return chirickModels;
    }

    @Override
    public List<ChirickViewModel> getRechiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User requester = this.userService.getByHandle(requesterHandle);

        User user = this.userService.getByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");

        Set<Chirick> rechiricks = user.getRechiricks();
        if (rechiricks.size() < 1) return null;

        List<Chirick> pagedRechiricks = this.chirickRepository.getChiricksInCollection(rechiricks, pageable);
        List<ChirickViewModel> chirickModels = new ArrayList<>(pagedRechiricks.size());
        for (Chirick chirick : pagedRechiricks) {
            chirickModels.add(this.mapActionPropertiesForChirick(chirick, requester));
        }

        return chirickModels;
    }

    @Override
    public List<ChirickViewModel> getLikesFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User requester = this.userService.getByHandle(requesterHandle);

        User user = this.userService.getByHandle(userHandle);
        if (user == null) throw new UserNotFoundException("User with handle " + userHandle + " was not found");

        Set<Chirick> likes = user.getLikes();
        if (likes.size() < 1) return null;

        List<Chirick> pagedLikes = this.chirickRepository.getChiricksInCollection(likes, pageable);
        List<ChirickViewModel> chirickModels = new ArrayList<>(pagedLikes.size());
        for (Chirick chirick : pagedLikes) {
            chirickModels.add(this.mapActionPropertiesForChirick(chirick, requester));
        }

        return chirickModels;
    }

    @Override
    public List<ChirickViewModel> getCommentsFromChirick(String postId, String requesterHandle, Pageable pageable) throws UserNotFoundException {
        User requester = this.userService.getByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException("No user with " + requesterHandle + " was found");

        List<Chirick> comments = this.chirickRepository.getAllCommentsFromPost(postId, pageable);

        List<ChirickViewModel> chirickModels = new ArrayList<>(comments.size());
        for (Chirick comment : comments) {
            chirickModels.add(this.mapActionPropertiesForChirick(comment, requester));
        }

        return chirickModels;
    }

    @Override
    public ChirickDetailsViewModel getChirickDetails(String chirickId, String requesterHandle) throws ChirickNotFoundException, UserNotFoundException {
        Chirick chirick = this.chirickRepository.findById(chirickId).orElse(null);
        if (chirick == null) throw new ChirickNotFoundException("Chirick is nowhere to be found");

        User requester = this.userService.getByHandle(requesterHandle);
        if (requester == null) throw new UserNotFoundException("No user with handle " + requesterHandle + " was found");

        ChirickDetailsViewModel details = new ChirickDetailsViewModel();

        ChirickViewModel chirickModel = this.mapActionPropertiesForChirick(chirick, requester);

        ChirickUserViewModel userDetails = this.mapper.map(chirick.getUser(), ChirickUserViewModel.class);

        details.setChirick(chirickModel);
        details.setUser(userDetails);
        details.setDate(chirick.getDate());

        return details;
    }
}