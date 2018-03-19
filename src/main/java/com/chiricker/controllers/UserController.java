package com.chiricker.controllers;

import com.chiricker.models.binding.UserLoginBindingModel;
import com.chiricker.models.binding.UserRegisterBindingModel;
import com.chiricker.models.entities.User;
import com.chiricker.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public ModelAndView register(@ModelAttribute("user") UserRegisterBindingModel user, ModelAndView modelAndView) {
        modelAndView.setViewName("users/register");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("user") UserRegisterBindingModel user, BindingResult result, ModelAndView modelAndView) {
        if (result.hasErrors()) {
            modelAndView.setViewName("users/register");
        } else {
            User registeredUser = this.userService.register(user);
            if (registeredUser == null) {
                modelAndView.setViewName("users/register");
                modelAndView.addObject("fatalError", "There was a problem with the register process, please try again");
            } else {
                modelAndView.setViewName("redirect:/login");
            }
        }

        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login(@ModelAttribute("user") UserLoginBindingModel user, @RequestParam(required = false) String error, ModelAndView modelAndView) {
        if (error != null) {
            modelAndView.addObject("error", "Handle and password do not match");
        }

        modelAndView.setViewName("users/login");
        return modelAndView;
    }
}