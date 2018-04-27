package com.chiricker.services.timelinePost;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.*;
import com.chiricker.areas.chiricks.repositories.TimelinePostRepository;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostServiceImpl;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
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
    private ModelMapper mapper;

    @InjectMocks
    private TimelinePostServiceImpl timelinePostService;

    private TimelineUserServiceModel testTimeline;
    private Chirick testChirick;
    private User testUser;
    private TimelinePostType type;
    private TimelinePost testPost;

    @Before
    public void setup() {
        this.testTimeline = new TimelineUserServiceModel() {{ setId(TIMELINE_ID); }};
        this.testTimeline.setUserId(TIMELINE_USER_ID);
        this.testChirick = new Chirick() {{ setId(CHIRICK_ID); }};
        this.testUser = new User() {{ setId(FROM_ID); }};
        this.type = POST_TYPE;
        this.testPost = new TimelinePost() {{
            setId(POST_ID);
            setTimeline(new Timeline() {{
                setId(TIMELINE_ID);
                setUser(new User() {{
                    setId(TIMELINE_USER_ID);
                }});
            }});
            setFrom(testUser);
            setTo(new User() {{ setId(TIMELINE_USER_ID); }});
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
        when(this.mapper.map(any(), eq(TimelinePostServiceModel.class))).thenAnswer(a -> {
            TimelinePost p = a.getArgument(0);
            TimelinePostServiceModel m = new TimelinePostServiceModel();
            m.setId(p.getId());
            m.setChirick(new ChirickServiceModel() {{ setId(p.getChirick().getId()); }});
            m.setTimeline(new TimelineServiceModel() {{ setId(p.getTimeline().getId()); }});
            m.setFrom(new UserServiceModelTP() {{ setId(p.getFrom().getId()); }});
            m.setTo(new UserServiceModelTP() {{ setId(p.getTo().getId()); }});
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
        TimelineUserServiceModel newTU = new TimelineUserServiceModel();
        newTU.setId("h34wsex7uyh35");
        newTU.setUserId("xcvrwssrg4w3");

        TimelinePostServiceModel post = this.timelinePostService.createPost(newTU, this.testChirick, this.testUser, this.type);
        this.testPost.getTimeline().setId("h34wsex7uyh35");
        this.testPost.getTo().setId("xcvrwssrg4w3");

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
        assertEquals("From user is not mapped correctly.", post.getFrom().getId(), compared.getFrom().getId());
        assertEquals("To user is not mapped correctly.", post.getTo().getId(), compared.getTo().getId());
        assertEquals("Post type is not mapped correctly.", post.getPostType(), compared.getPostType());
    }
}
