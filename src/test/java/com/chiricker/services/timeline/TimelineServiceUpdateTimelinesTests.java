package com.chiricker.services.timeline;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.entities.Timeline;
import com.chiricker.areas.chiricks.models.entities.TimelinePost;
import com.chiricker.areas.chiricks.models.entities.enums.TimelinePostType;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelinePostServiceModel;
import com.chiricker.areas.chiricks.models.service.TimelineUserServiceModel;
import com.chiricker.areas.chiricks.repositories.TimelineRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickService;
import com.chiricker.areas.chiricks.services.timeline.TimelineServiceImpl;
import com.chiricker.areas.chiricks.services.timelinePost.TimelinePostService;
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
import java.util.Optional;
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

    private static final String POST_ID = "11";
    private static final String USER_HANDLE = "pesho";
    private static final String USER_ID = "gg4g4g4";

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

    @Before
    public void setup() {
        this.testChirick = new Chirick();
        this.testChirick.setId("h45h3hh55h4");

        ChirickServiceModel testChirickModel = new ChirickServiceModel();
        testChirickModel.setId(this.testChirick.getId());

        TimelinePostServiceModel postModel = new TimelinePostServiceModel();
        postModel.setId("asdsad");
        postModel.setChirick(testChirickModel);

        TimelineUserServiceModel tu = new TimelineUserServiceModel() {{
            setId("1");
            setUserId("2");
        }};
        Timeline t = new Timeline() {{
            setPosts(new HashSet<>() {{
                setId("1");
                add(new TimelinePost() {{ setId(POST_ID); }});
            }});
        }};
        TimelineUserServiceModel tu2 = new TimelineUserServiceModel() {{
            setId("3");
            setUserId("4");
        }};
        Timeline t2 = new Timeline() {{
            setPosts(new HashSet<>() {{
                setId("3");
                add(new TimelinePost() {{ setId("43"); }});
            }});
        }};
        Set<TimelineUserServiceModel> timelineUserModels = new HashSet<>();
        timelineUserModels.add(tu);
        timelineUserModels.add(tu2);

        when(this.chirickService.getById(this.testChirick.getId())).thenReturn(testChirickModel);
        when(this.mapper.map(eq(testChirickModel), eq(Chirick.class))).thenReturn(this.testChirick);
        when(this.userService.getIdForHandle(USER_HANDLE)).thenReturn(USER_ID);
        when(this.userService.getUserFollowerTimelineIds(USER_ID)).thenReturn(timelineUserModels);
        when(this.timelinePostService.getPostIdByFields(any(TimelineUserServiceModel.class), eq(testChirick), any(User.class), any(TimelinePostType.class))).thenReturn(POST_ID);
        when(this.timelineRepository.findById(tu.getId())).thenReturn(Optional.of(t));
        when(this.timelineRepository.findById(tu2.getId())).thenReturn(Optional.of(t2));
    }

    @Test
    public void testUpdateTimelines_WithInvalidChirickId_ShouldReturnNull() {
        Future result = this.timelineService.updateTimeline(USER_HANDLE, "ggg444", TimelinePostType.COMMENT, true);

        assertEquals("Timelines should be null.", result, null);
    }

    @Test
    public void testUpdateTimelines_WithInvalidUserHandle_ShouldReturnNull() {
        Future future = this.timelineService.updateTimeline("asdsadsad", this.testChirick.getId(), TimelinePostType.COMMENT, false);

        assertEquals("Result should be null.", future, null);
    }

    @Test
    public void testUpdateTimelines_WithValidData_ShouldRemoveChirickFromOneOutOfTwoTimelines() throws ExecutionException, InterruptedException {
        Future future = this.timelineService.updateTimeline(USER_HANDLE, this.testChirick.getId(), TimelinePostType.CHIRICK, false);
        Set<Timeline> timelines = (Set<Timeline>) future.get();

        Timeline tlWithZeroPosts = timelines.stream().filter(t -> t.getId().equals("1")).findFirst().get();
        Timeline tlWithOnePosts = timelines.stream().filter(t -> t.getId().equals("3")).findFirst().get();

        assertTrue("Timeline should not have chirick.", tlWithZeroPosts.getPosts().size() == 0);
        assertTrue("Timeline should have one post.", tlWithOnePosts.getPosts().size() == 1);
    }
}