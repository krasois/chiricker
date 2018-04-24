package com.chiricker.areas.chiricks.controllers;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.exceptions.ChirickNotValidException;
import com.chiricker.areas.chiricks.models.binding.CommentBindingModel;
import com.chiricker.areas.chiricks.models.binding.LikeBindingModel;
import com.chiricker.areas.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.view.ChirickCommentResultViewModel;
import com.chiricker.areas.chiricks.models.view.ChirickLikeResultViewModel;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.models.view.RechirickResultViewModel;
import com.chiricker.areas.chiricks.services.timeline.TimelineService;
import com.chiricker.areas.logger.annotations.Logger;
import com.chiricker.areas.logger.models.entities.enums.Operation;
import com.chiricker.controllers.BaseController;
import com.chiricker.areas.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/chirick")
public class ChirickRestController extends BaseController {

    private final ChirickService chirickService;
    private final TimelineService timelineService;

    @Autowired
    public ChirickRestController(ChirickService chirickService, TimelineService timelineService) {
        this.chirickService = chirickService;
        this.timelineService = timelineService;
    }

    @PostMapping(value = "/add")
    @Logger(entity = Chirick.class, operation = Operation.CHIRICK)
    public String chirick(@Valid ChirickBindingModel chirick, BindingResult result, Principal principal) throws ChirickNotValidException, UserNotFoundException {
        if (result.hasErrors()) throw new ChirickNotValidException(result.getFieldError("chirick").getDefaultMessage());
        ChirickServiceModel serviceModel = this.chirickService.add(chirick, principal.getName());
        this.timelineService.updateTimeline(principal.getName(), serviceModel.getId(), TimelinePostType.CHIRICK, true);
        return "You chiricked successfully!";
    }

    @ResponseBody
    @PostMapping("/rechirick")
    @Logger(entity = Chirick.class, operation = Operation.RECHIRICK)
    public RechirickResultViewModel rechirick(RechirickBindingModel model, Principal principal) throws UserNotFoundException, ChirickNotFoundException {
        RechirickResultViewModel resultViewModel = this.chirickService.rechirick(model, principal.getName());
        this.timelineService.updateTimeline(principal.getName(), resultViewModel.getId(), TimelinePostType.RECHIRICK, resultViewModel.isRechiricked());
        return resultViewModel;
    }

    @ResponseBody
    @PostMapping("/like")
    @Logger(entity = Chirick.class, operation = Operation.LIKE)
    public ChirickLikeResultViewModel like(LikeBindingModel model, Principal principal) throws UserNotFoundException, ChirickNotFoundException {
        ChirickLikeResultViewModel resultViewModel = this.chirickService.like(model, principal.getName());
        this.timelineService.updateTimeline(principal.getName(), resultViewModel.getId(), TimelinePostType.LIKE, resultViewModel.isLiked());
        return resultViewModel;
    }

    @ResponseBody
    @PostMapping("/comment")
    @Logger(entity = Chirick.class, operation = Operation.COMMENT)
    public ChirickCommentResultViewModel comment(@Valid CommentBindingModel model, BindingResult result, Principal principal) throws UserNotFoundException, ChirickNotFoundException, ChirickNotValidException {
        if (result.hasErrors()) throw new ChirickNotValidException(result.getFieldError("comment").getDefaultMessage());
        ChirickCommentResultViewModel resultViewModel = this.chirickService.comment(model, principal.getName());
        this.timelineService.updateTimeline(principal.getName(), resultViewModel.getId(), TimelinePostType.COMMENT, true);
        return resultViewModel;
    }

    @GetMapping("/@{handle}/chiricks")
    public ResponseEntity<List<ChirickViewModel>> getChiricksFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<ChirickViewModel> models = this.chirickService.getChiricksFromUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/@{handle}/comments")
    public ResponseEntity<List<ChirickViewModel>> getCommentsFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<ChirickViewModel> models = this.chirickService.getCommentsFromUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/@{handle}/rechiricks")
    public ResponseEntity<List<ChirickViewModel>> getRechiricksFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<ChirickViewModel> models = this.chirickService.getRechiricksFromUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/@{handle}/likes")
    public ResponseEntity<List<ChirickViewModel>> getLikesFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<ChirickViewModel> models = this.chirickService.getLikesFromUser(handle, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<ChirickViewModel>> getCommentsForChirick(@PathVariable("postId") String postId, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        List<ChirickViewModel> models = this.chirickService.getCommentsFromChirick(postId, principal.getName(), pageable);
        return new ResponseEntity<>(models, HttpStatus.OK);
    }
}