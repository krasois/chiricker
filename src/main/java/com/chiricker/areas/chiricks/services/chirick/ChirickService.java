package com.chiricker.areas.chiricks.services.chirick;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.areas.chiricks.models.binding.CommentBindingModel;
import com.chiricker.areas.chiricks.models.binding.LikeBindingModel;
import com.chiricker.areas.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.view.*;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChirickService {

    Chirick getById(String chirickId);

    ChirickServiceModel add(ChirickBindingModel bindingModel, String handle) throws UserNotFoundException;

    RechirickResultViewModel rechirick(RechirickBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    ChirickLikeResultViewModel like(LikeBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    ChirickCommentResultViewModel comment(CommentBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    List<ChirickViewModel> getChiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getCommentsFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getRechiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getLikesFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getCommentsFromChirick(String postId, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    ChirickDetailsViewModel getChirickDetails(String chirickId, String requesterHandle) throws ChirickNotFoundException, UserNotFoundException;
}
