package com.chiricker.chiricks.models.binding;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.chiricker.util.WebConstants.*;

public class ChirickCommentBindingModel {

    private String id;

    @NotEmpty(message = CHIRICK_EMPTY)
    @Size(min = CHIRICK_MIN, max = CHIRICK_MAX, message = CHIRICK_LENGTH)
    private String comment;

    public ChirickCommentBindingModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
