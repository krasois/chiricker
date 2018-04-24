package com.chiricker.util.mapper;

import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomMapper {

    private final ModelMapper mapper;

    @Autowired
    public CustomMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public TimelinePostServiceModel postToServiceModel(TimelinePost post) {
        TimelinePostServiceModel mappedPost = new TimelinePostServiceModel();
        mappedPost.setId(post.getId());
        mappedPost.setDate(post.getDate());
        mappedPost.setPostType(post.getPostType());
        mappedPost.setTimeline(this.mapper.map(post.getTimeline(), TimelineServiceModel.class));
        mappedPost.setFrom(this.mapper.map(post.getFrom(), UserServiceModel.class));
        mappedPost.setTo(this.mapper.map(post.getTo(), UserServiceModel.class));
        mappedPost.setChirick(this.mapper.map(post.getChirick(), ChirickServiceModel.class));
        return mappedPost;
    }
}