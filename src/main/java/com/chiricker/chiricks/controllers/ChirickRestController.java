package com.chiricker.chiricks.controllers;

import com.chiricker.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.chiricks.exceptions.ChirickNotValidException;
import com.chiricker.chiricks.models.binding.ChirickCommentBindingModel;
import com.chiricker.chiricks.models.binding.ChirickLikeBindingModel;
import com.chiricker.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.chiricks.models.view.ChirickCommentResultViewModel;
import com.chiricker.chiricks.models.view.ChirickLikeResultViewModel;
import com.chiricker.chiricks.models.view.ChirickViewModel;
import com.chiricker.chiricks.models.view.RechirickResultViewModel;
import com.chiricker.general.controllers.BaseController;
import com.chiricker.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.chiricks.services.chirick.ChirickService;
import com.chiricker.users.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/chirick")
public class ChirickRestController extends BaseController {

    private final ChirickService chirickService;

    @Autowired
    public ChirickRestController(ChirickService chirickService) {
        this.chirickService = chirickService;
    }

    @PostMapping(value = "/add")
    public String chirick(@Valid ChirickBindingModel chirick, BindingResult result, Principal principal) throws ChirickNotValidException, UserNotFoundException {
        if (result.hasErrors()) throw new ChirickNotValidException(result.getFieldError("chirick").getDefaultMessage());
        this.chirickService.add(chirick, principal.getName());
        return "You chiricked successfully!";
    }

    @GetMapping("/@{handle}/chiricks")
    public List<ChirickViewModel> getChiricksFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) {
        return this.chirickService.getChiricksFromUser(handle, principal.getName(), pageable);
    }

    @GetMapping("/@{handle}/comments")
    public List<ChirickViewModel> getCommentsFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) {
        return this.chirickService.getCommentsFromUser(handle, principal.getName(), pageable);
    }

    @GetMapping("/@{handle}/rechiricks")
    public List<ChirickViewModel> getRechiricksFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        return this.chirickService.getRechiricksFromUser(handle, principal.getName(), pageable);
    }

    @GetMapping("/@{handle}/likes")
    public List<ChirickViewModel> getLikesFromUser(@PathVariable("handle") String handle, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        return this.chirickService.getLikesFromUser(handle, principal.getName(), pageable);
    }

    @GetMapping("/comments/{postId}/all")
    public List<ChirickViewModel> getCommentsForChirick(@PathVariable("postId") String postId, @PageableDefault(size = 15) Pageable pageable, Principal principal) throws UserNotFoundException {
        return this.chirickService.getCommentsFromChirick(postId, principal.getName(), pageable);
    }

    @PostMapping("/rechirick")
    public RechirickResultViewModel rechirick(RechirickBindingModel model, Principal principal) throws UserNotFoundException, ChirickNotFoundException {
        return this.chirickService.rechirick(model, principal.getName());
    }

    @PostMapping("/like")
    public ChirickLikeResultViewModel like(ChirickLikeBindingModel model, Principal principal) throws UserNotFoundException, ChirickNotFoundException {
        return this.chirickService.like(model, principal.getName());
    }

    @PostMapping("/comment")
    public ChirickCommentResultViewModel comment(@Valid ChirickCommentBindingModel model, BindingResult result, Principal principal) throws UserNotFoundException, ChirickNotFoundException, ChirickNotValidException {
        if (result.hasErrors()) throw new ChirickNotValidException(result.getFieldError("comment").getDefaultMessage());
        return this.chirickService.comment(model, principal.getName());
    }
}