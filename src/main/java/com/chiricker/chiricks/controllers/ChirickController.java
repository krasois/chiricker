package com.chiricker.chiricks.controllers;

import com.chiricker.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.chiricks.services.chirick.ChirickService;
import com.chiricker.general.controllers.BaseController;
import com.chiricker.users.exceptions.UserNotFoundException;
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
