package com.chiricker.chiricks.services.chirick;

import com.chiricker.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.chiricks.models.binding.ChirickCommentBindingModel;
import com.chiricker.chiricks.models.binding.ChirickLikeBindingModel;
import com.chiricker.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.chiricks.models.entities.Chirick;
import com.chiricker.chiricks.models.view.*;
import com.chiricker.users.exceptions.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

public interface ChirickService {

    Chirick add(ChirickBindingModel bindingModel, String handle) throws UserNotFoundException;

    RechirickResultViewModel rechirick(RechirickBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    ChirickLikeResultViewModel like(ChirickLikeBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    ChirickCommentResultViewModel comment(ChirickCommentBindingModel model, String handle) throws UserNotFoundException, ChirickNotFoundException;

    List<ChirickViewModel> getChiricksFromUser(String userHandle, String requesterHandle, Pageable pageable);

    List<ChirickViewModel> getCommentsFromUser(String userHandle, String requesterHandle, Pageable pageable);

    List<ChirickViewModel> getRechiricksFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getLikesFromUser(String userHandle, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    List<ChirickViewModel> getCommentsFromChirick(String postId, String requesterHandle, Pageable pageable) throws UserNotFoundException;

    ChirickDetailsViewModel getChirickDetails(String chirickId, String requesterHandle) throws ChirickNotFoundException, UserNotFoundException;
}
