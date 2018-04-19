package com.chiricker.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timeline.TimelineServiceImpl;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimelineServiceUpdateTimelinesTests {

    @Mock
    private TimelineRepository timelineRepository;
    @Mock
    private UserService userService;
    @Mock
    private ChirickService chirickService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private TimelineServiceImpl timelineService;

    private Chirick testChirick;
    private User testUser;
    private Set<TimelinePost> posts;

    @Before
    public void setup() {
        UserServiceModel testUserModel = new UserServiceModel();
        ChirickServiceModel testChirickModel = new ChirickServiceModel();

        this.testChirick = new Chirick();
        this.testChirick.setId("h45h3hh55h4");
        this.testUser = new User();
        this.testUser.setId("gg4g4g4");
        this.testUser.setHandle("pesho");

        this.posts = new HashSet<>();

        this.testUser.setFollowers(new HashSet<>());

        when(this.chirickService.getById(this.testChirick.getId())).thenReturn(testChirickModel);
        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(testUserModel);
        when(this.mapper.map(eq(testChirickModel), eq(Chirick.class))).thenReturn(this.testChirick);
        when(this.mapper.map(eq(testUserModel), eq(User.class))).thenReturn(this.testUser);
    }

    @Test
    public void testUpdateTimelines_WithInvalidChirickId_ShouldReturnNull() throws UserNotFoundException {
        Future result = this.timelineService.updateTimeline(this.testUser.getHandle(), "ggg444", TimelinePostType.COMMENT, true);

        assertEquals("Timelines should be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateTimelines_WithInvalidUserHandle_ShouldThrowUserNotFound() throws UserNotFoundException {
        this.timelineService.updateTimeline("asdsadsad", this.testChirick.getId(), TimelinePostType.COMMENT, false);
    }

    @Test
    public void testUpdateTimelines_WithValidData_ShouldAddChirickToTimelines() throws UserNotFoundException, ExecutionException, InterruptedException {
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
    public void testUpdateTimelines_WithValidData_ShouldRemoveChirickFromOneOutOfTwoTimelines() throws UserNotFoundException, ExecutionException, InterruptedException {
        this.testUser.getFollowers().add(
                new User() {{
                    setTimeline(new Timeline() {{
                        setPosts(new HashSet<>() {{
                            add(new TimelinePost() {{
                                setPostType(TimelinePostType.CHIRICK);
                                setUser(testUser);
                                setChirick(testChirick);
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
                                setPostType(TimelinePostType.CHIRICK);
                                setUser(new User() {{ setId("cvbvb"); }});
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