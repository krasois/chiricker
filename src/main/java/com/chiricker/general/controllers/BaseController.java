package com.chiricker.general.controllers;

import org.springframework.web.servlet.ModelAndView;

public class BaseController {

    private static final String BASE_LAYOUT_NAME = "base-layout";
    private static final String VIEW_MODEL_NAME = "view";
    private static final String REDIRECT_ACTION = "redirect:";

    private ModelAndView finalize(ModelAndView modelAndView) {
        modelAndView.setViewName(BASE_LAYOUT_NAME);
        return modelAndView;
    }

    protected ModelAndView view(String viewName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(VIEW_MODEL_NAME, viewName);
        return this.finalize(modelAndView);
    }

    protected ModelAndView view(String viewName, String modelName, Object model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(VIEW_MODEL_NAME, viewName);
        modelAndView.addObject(modelName, model);
        return this.finalize(modelAndView);
    }

    protected ModelAndView redirect(String url) {
        return new ModelAndView(REDIRECT_ACTION + url);
    }
}