package com.chiricker.models.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("users/register");
    }

    @PostMapping("/register")
    public ModelAndView register(ModelAndView modelAndView) {
        System.out.println();
        return modelAndView;
    }
}
