package com.chiricker.chiricks.models.binding;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.chiricker.util.WebConstants.*;

public class ChirickBindingModel {

    @NotEmpty(message = CHIRICK_EMPTY)
    @Size(min = CHIRICK_MIN, max = CHIRICK_MAX, message = CHIRICK_LENGTH)
    private String chirick;

    public ChirickBindingModel() {
    }

    public String getChirick() {
        return chirick;
    }

    public void setChirick(String chirick) {
        this.chirick = chirick;
    }
}