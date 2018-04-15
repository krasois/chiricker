package com.chiricker.areas.chiricks.controllers;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.controllers.BaseController;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class ChirickController extends BaseController {

    private final ChirickService chirickService;

    @Autowired
    public ChirickController(ChirickService chirickService) {
        this.chirickService = chirickService;
    }

    @GetMapping("/@{handle}/{chirickId}")
    public ModelAndView chirickDetails(@PathVariable("chirickId") String chirickId, Principal principal) throws ChirickNotFoundException, UserNotFoundException {
        return this.view("chiricks/details", "details", this.chirickService.getChirickDetails(chirickId, principal.getName()));
    }
}
