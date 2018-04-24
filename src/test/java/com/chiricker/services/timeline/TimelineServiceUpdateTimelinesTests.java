package com.chiricker.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timeline.TimelineServiceImpl;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostService;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimelineServiceUpdateTimelinesTests {

    private static final String POST_ID = "postid";

    @Mock
    private UserService userService;
    @Mock
    private ChirickService chirickService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private TimelinePostService timelinePostService;
    @Mock
    private TimelineRepository timelineRepository;

    @InjectMocks
    private TimelineServiceImpl timelineService;

    private Chirick testChirick;
    private User testUser;

    @Before
    public void setup() {
        this.testChirick = new Chirick();
        this.testChirick.setId("h45h3hh55h4");
        this.testUser = new User();
        this.testUser.setId("gg4g4g4");
        this.testUser.setHandle("pesho");

        ChirickServiceModel testChirickModel = new ChirickServiceModel();
        testChirickModel.setId(this.testChirick.getId());

        this.testUser.setFollowers(new HashSet<>());

        TimelinePostServiceModel postModel = new TimelinePostServiceModel();
        postModel.setId("asdsad");
        postModel.setDate(null);
        postModel.setPostType(null);
        postModel.setChirick(testChirickModel);
        postModel.setFrom(null);
        postModel.setTo(null);
        postModel.setTimeline(null);

        when(this.chirickService.getById(this.testChirick.getId())).thenReturn(testChirickModel);
        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(testUser);
        when(this.mapper.map(eq(testChirickModel), eq(Chirick.class))).thenReturn(this.testChirick);
        when(this.timelinePostService.getPostIdByFields(any(Timeline.class), any(Chirick.class), any(User.class), any(TimelinePostType.class))).thenReturn(POST_ID);
        when(this.timelinePostService.createPost(any(Timeline.class), any(Chirick.class), any(User.class), any(TimelinePostType.class))).thenReturn(postModel);
    }

    @Test
    public void testUpdateTimelines_WithInvalidChirickId_ShouldReturnNull() {
        Future result = this.timelineService.updateTimeline(this.testUser.getHandle(), "ggg444", TimelinePostType.COMMENT, true);

        assertEquals("Timelines should be null.", result, null);
    }

    @Test
    public void testUpdateTimelines_WithInvalidUserHandle_ShouldReturnNull() {
        Future future = this.timelineService.updateTimeline("asdsadsad", this.testChirick.getId(), TimelinePostType.COMMENT, false);

        assertEquals("Result should be null.", future, null);
    }

    @Test
    public void testUpdateTimelines_WithValidData_ShouldAddChirickToTimelines() throws ExecutionException, InterruptedException {
        this.testUser.getFollowers().add(
                new User() {{
                    setTimeline(new Timeline() {{
                        setPosts(new HashSet<>());
                        setId("3g33g4");
                    }});
                }}
        );
        this.testUser.getFollowers().add(
                new User() {{
                    setTimeline(new Timeline() {{
                        setPosts(new HashSet<>());
                        setId("uyununyb4");
                    }});
                }}
        );

        Future future = this.timelineService.updateTimeline(this.testUser.getHandle(), this.testChirick.getId(), TimelinePostType.CHIRICK, true);
        Set<Timeline> timelines = (Set<Timeline>) future.get();
        for (Timeline timeline : timelines) {
            assertTrue("Timeline with ID '" + "' does not have the new chirick.", timeline.getPosts()
                    .stream()
                    .anyMatch(p -> p.getChirick()
                            .getId()
                            .equals(this.testChirick.getId())));
        }
    }

    @Test
    public void testUpdateTimelines_WithValidData_ShouldRemoveChirickFromOneOutOfTwoTimelines() throws ExecutionException, InterruptedException {
        this.testUser.getFollowers().add(
                new User() {{
                    setTimeline(new Timeline() {{
                        setPosts(new HashSet<>() {{
                            add(new TimelinePost() {{
                                setId(POST_ID);
                                setPostType(TimelinePostType.CHIRICK);
                                setFrom(testUser);
                                setChirick(new Chirick() {{ setId("ccccccc"); }});
                            }});
                        }});
                        setId("3g33g4");
                    }});
                }}
        );
        this.testUser.getFollowers().add(
                new User() {{
                    setTimeline(new Timeline() {{
                        setPosts(new HashSet<>() {{
                            add(new TimelinePost() {{
                                setId("bbbb");
                                setPostType(TimelinePostType.CHIRICK);
                                setFrom(new User() {{ setId("cvbvb"); }});
                                setChirick(testChirick);
                            }});
                        }});
                        setId("uyununyb4");
                    }});
                }}
        );

        Future future = this.timelineService.updateTimeline(this.testUser.getHandle(), this.testChirick.getId(), TimelinePostType.CHIRICK, false);
        Set<Timeline> timelines = (Set<Timeline>) future.get();

        Timeline tlWithZeroPosts = timelines.stream().filter(t -> t.getId().equals("3g33g4")).findFirst().get();
        Timeline tlWithOnePosts = timelines.stream().filter(t -> t.getId().equals("uyununyb4")).findFirst().get();

        assertTrue("Timeline should not have chirick.", tlWithZeroPosts.getPosts().size() == 0);
        assertTrue("Timeline should have one post.", tlWithOnePosts.getPosts().size() == 1);
        assertTrue("Timeline should have the chirick.", tlWithOnePosts.getPosts()
                .stream()
                .anyMatch(p -> p.getChirick()
                        .getId()
                        .equals(testChirick.getId())));
    }
}