package com.chiricker.areas.chiricks.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TimelinePostServiceImpl implements TimelinePostService {

    private final TimelinePostRepository timelinePostRepository;
    private final ModelMapper mapper;

    @Autowired
    public TimelinePostServiceImpl(TimelinePostRepository timelinePostRepository, ModelMapper mapper) {
        this.timelinePostRepository = timelinePostRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<TimelinePostServiceModel> getPostsInCollection(Set<TimelinePost> posts, Pageable pageable) {
        Page<TimelinePost> pagedPosts = this.timelinePostRepository.findAllInSet(posts, pageable);

        return pagedPosts.map(p -> mapper.map(p, TimelinePostServiceModel.class));
    }
}