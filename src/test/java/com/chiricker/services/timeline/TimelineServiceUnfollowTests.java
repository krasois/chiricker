//package com.chiricker.services.timeline;
//
//import com.chiricker.areas.chiricks.models.entities.Chirick;
//import com.chiricker.areas.chiricks.models.entities.Timeline;
//import com.chiricker.areas.chiricks.models.entities.TimelinePost;
//import com.chiricker.areas.chiricks.repositories.TimelineRepository;
//import com.chiricker.areas.chiricks.services.timeline.TimelineServiceImpl;
//import com.chiricker.areas.users.exceptions.UserNotFoundException;
//import com.chiricker.areas.users.models.entities.User;
//import com.chiricker.areas.users.models.service.UserServiceModel;
//import com.chiricker.areas.users.services.user.UserService;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.HashSet;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//@ActiveProfiles("test")
//public class TimelineServiceUnfollowTests {
//
//    @Mock
//    private TimelineRepository timelineRepository;
//    @Mock
//    private UserService userService;
//    @Mock
//    private ModelMapper mapper;
//
//    @InjectMocks
//    private TimelineServiceImpl timelineService;
//
//    private UserServiceModel testRequester;
//    private UserServiceModel testUser;
//    private Chirick testChirick;
//    private User user;
//
//    @Before
//    public void setup() {
//        this.testRequester = new UserServiceModel();
//        this.testRequester.setHandle("pesho");
//        this.testRequester.setId("wgwgw4eg3wg3wg3w23gwg3w2");
//
//        this.testUser = new UserServiceModel();
//        this.testUser.setHandle("gosho");
//        this.testUser.setId("wf4gfg4g44t4gg");
//
//        user = new User();
//        user.setHandle(this.testUser.getHandle());
//        user.setId(this.testUser.getId());
//
//        this.testChirick = new Chirick();
//        this.testChirick.setChirick("some chirick");
//
//        User requester = new User();
//        requester.setHandle(this.testRequester.getHandle());
//        requester.setId(this.testRequester.getId());
//        requester.setTimeline(new Timeline());
//        requester.getTimeline().setPosts(new HashSet<>());
//        requester.getTimeline().getPosts().add(new TimelinePost() {{ setPoster(user); }});
//        requester.getTimeline().getPosts().add(new TimelinePost() {{
//            setChirick(testChirick);
//            setPoster(new User() {{
//                setId("cvbcvbcbdddbfbc");
//            }});
//        }});
//        requester.getTimeline().getPosts().add(new TimelinePost() {{ setPoster(user); }});
//
//
//        when(this.userService.getByHandle(this.testRequester.getHandle())).thenReturn(this.testRequester);
//        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(this.testUser);
//        when(this.mapper.map(eq(this.testRequester), eq(User.class))).thenReturn(requester);
//        when(this.mapper.map(eq(this.testUser), eq(User.class))).thenReturn(this.user);
//        when(this.timelineRepository.save(any())).thenAnswer(a -> a.getArgument(0));
//    }
//
//    @Test(expected = UserNotFoundException.class)
//    public void testUnfollow_WithInvalidUserHandle_ShouldThrow() throws UserNotFoundException {
////        this.timelineService.unfollow("asdasd", this.testRequester.getHandle());
//    }
//
////    @Test(expected = UserNotFoundException.class)
////    public void testUnfollow_WithInvalidRequesterHandle_ShouldThrow() throws UserNotFoundException {
////        this.timelineService.unfollow(this.testUser.getHandle(), "asdsad");
////    }
////
////    @Test
////    public void testUnfollow_WithValidData_ShouldReturnTimelieWithRemovedPosts() throws UserNotFoundException, ExecutionException, InterruptedException {
////        Future result = this.timelineService.unfollow(this.testUser.getHandle(), this.testRequester.getHandle());
////        Timeline timeline = (Timeline) result.get();
////
////        assertTrue("Timeline has more or less than 1 posts.", timeline.getPosts().size() == 1);
////        assertTrue("The wrong timeline posts are removed.", timeline.getPosts()
////                .stream()
////                .anyMatch(p -> p.getChirick()
////                        .getChirick()
////                        .equals(this.testChirick.getChirick())));
////    }
//}