package com.chiricker.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostServiceImpl;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.util.mapper.CustomMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimelinePostServiceCreatePostTests {

    private static final String TIMELINE_ID = "yyttyyt";
    private static final String TIMELINE_USER_ID = "vcvcvcvdf";
    private static final String CHIRICK_ID = "btrb aqa";
    private static final String FROM_ID = "q2r3wqtw3";
    private static final String POST_ID = "vbcbftbghtr";
    private static final TimelinePostType POST_TYPE = TimelinePostType.RECHIRICK;

    @Mock
    private TimelinePostRepository timelinePostRepository;
    @Mock
    private CustomMapper customMapper;

    @InjectMocks
    private TimelinePostServiceImpl timelinePostService;

    private Timeline testTimeline;
    private Chirick testChirick;
    private User testUser;
    private TimelinePostType type;
    private TimelinePost testPost;

    @Before
    public void setup() {
        this.testTimeline = new Timeline() {{ setId(TIMELINE_ID); }};
        this.testTimeline.setUser(new User() {{ setId(TIMELINE_USER_ID); }});
        this.testChirick = new Chirick() {{ setId(CHIRICK_ID); }};
        this.testUser = new User() {{ setId(FROM_ID); }};
        this.type = POST_TYPE;
        this.testPost = new TimelinePost() {{
            setId(POST_ID);
            setTimeline(testTimeline);
            setFrom(testUser);
            setTo(testTimeline.getUser());
            setChirick(testChirick);
            setPostType(type);
        }};

        when(this.timelinePostRepository.findByChirickIdAndFromIdAndToIdAndTimelineIdAndPostType(eq(CHIRICK_ID),
                eq(FROM_ID), eq(TIMELINE_USER_ID), eq(TIMELINE_ID), eq(POST_TYPE))).thenReturn(this.testPost);
        when(this.timelinePostRepository.save(any())).thenAnswer(a -> {
            TimelinePost p = a.getArgument(0);
            p.setId(POST_ID);
            return p;
        });
        when(this.customMapper.postToServiceModel(any())).thenAnswer(a -> {
            TimelinePost p = a.getArgument(0);
            TimelinePostServiceModel m = new TimelinePostServiceModel();
            m.setId(p.getId());
            m.setChirick(new ChirickServiceModel() {{ setId(p.getChirick().getId()); }});
            m.setTimeline(new TimelineServiceModel() {{ setId(p.getTimeline().getId()); }});
            m.getTimeline().setUser(new UserServiceModel() {{ setId(p.getTimeline().getUser().getId()); }});
            m.setFrom(new UserServiceModel() {{ setId(p.getFrom().getId()); }});
            m.setTo(new UserServiceModel() {{ setId(p.getTimeline().getUser().getId()); }});
            m.setPostType(p.getPostType());
            return m;
        });
    }

    @Test
    public void testCreatePost_WithValidData_ShouldMapCorrectly() {
        TimelinePostServiceModel post = this.timelinePostService.createPost(this.testTimeline, this.testChirick, this.testUser, this.type);

        this.testMapping(post, this.testPost);
    }

    @Test
    public void testCreatePost_WithInvalidTimeline_ShouldSaveAndMapCorrectly() {
        this.testPost.getTimeline().setId("87008707");
        TimelinePostServiceModel post = this.timelinePostService.createPost(this.testPost.getTimeline(), this.testChirick, this.testUser, this.type);

        this.testMapping(post, this.testPost);
    }

    @Test
    public void testCreatePost_WithInvalidChirick_ShouldSaveAndMapCorrectly() {
        this.testPost.getChirick().setId("677667hr6hdht");
        TimelinePostServiceModel post = this.timelinePostService.createPost(this.testTimeline, this.testPost.getChirick(), this.testUser, this.type);

        this.testMapping(post, this.testPost);
    }

    @Test
    public void testCreatePost_WithInvalidUser_ShouldSaveAndMapCorrectly() {
        this.testPost.getFrom().setId("2242342");
        TimelinePostServiceModel post = this.timelinePostService.createPost(this.testTimeline, this.testChirick, this.testPost.getFrom(), this.type);

        this.testMapping(post, this.testPost);
    }

    @Test
    public void testCreatePost_WithInvalidType_ShouldSaveAndMapCorrectly() {
        this.testPost.setPostType(TimelinePostType.COMMENT);
        TimelinePostServiceModel post = this.timelinePostService.createPost(this.testTimeline, this.testChirick, this.testUser, this.testPost.getPostType());

        this.testMapping(post, this.testPost);
    }

    private void testMapping(TimelinePostServiceModel post, TimelinePost compared) {
        assertEquals("Post id is not mapped correctly.", post.getId(), compared.getId());
        assertEquals("Chirick is not mapped correctly.", post.getChirick().getId(), compared.getChirick().getId());
        assertEquals("Timeline is not mapped correctly.", post.getTimeline().getId(), compared.getTimeline().getId());
        assertEquals("Timeline user is not mapped correctly.", post.getTimeline().getUser().getId(), compared.getTimeline().getUser().getId());
        assertEquals("From user is not mapped correctly.", post.getFrom().getId(), compared.getFrom().getId());
        assertEquals("To user is not mapped correctly.", post.getTo().getId(), compared.getTo().getId());
        assertEquals("Post type is not mapped correctly.", post.getPostType(), compared.getPostType());
    }
}
