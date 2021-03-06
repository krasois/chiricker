package com.chiricker.services.chirick;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.view.ChirickViewModel;
import com.chiricker.areas.chiricks.repositories.ChirickRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickServiceImpl;
import com.chiricker.areas.users.exceptions.UserNotFoundException;
import com.chiricker.areas.users.models.entities.Profile;
import com.chiricker.areas.users.models.entities.User;
import com.chiricker.areas.users.models.service.SimpleUserServiceModel;
import com.chiricker.areas.users.models.service.UserServiceModel;
import com.chiricker.areas.users.services.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChirickServiceGetActivityFromUserTests {

    private static final String USER_HANDLE = "gosho";
    private static final String REQUESTER_HANDLE = "pesho";
    private static final String REQUESTER_NAME = "Pesho";
    private static final String USER_ID = "whgwgwgsea53a5";
    private static final String REQUESTER_ID = "ggrhtrhtdhhdhdhd";
    private static final String USER_PIC_URL = "243623643363y5h54eh54h45h45";

    @Mock
    private ChirickRepository chirickRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ChirickServiceImpl chirickService;

    private User testUser;
    private User testRequester;
    private Pageable pageable;
    private Chirick testChirick;

    @Before
    public void setup() {
        this.pageable = Mockito.mock(Pageable.class);

        this.testRequester = new User();
        this.testRequester.setHandle(REQUESTER_HANDLE);
        this.testRequester.setName(REQUESTER_NAME);
        this.testRequester.setId(REQUESTER_ID);
        this.testRequester.setProfile(new Profile());
        this.testRequester.getProfile().setProfilePicUrl(USER_PIC_URL);

        this.testUser = new User();
        this.testUser.setHandle(USER_HANDLE);
        this.testUser.setName(REQUESTER_NAME);
        this.testUser.setId(USER_ID);
        this.testUser.setProfile(new Profile());
        this.testUser.getProfile().setProfilePicUrl(USER_PIC_URL);
        this.testUser.setRechiricks(new HashSet<>());
        this.testUser.setLikes(new HashSet<>());

        this.testChirick = new Chirick() {{
            setChirick("asdawfw3awf");
            setUser(testUser);
            setRechiricks(new HashSet<>());
            setComments(new HashSet<>());
            setLikes(new HashSet<>());
            getRechiricks().add(testRequester);
            getLikes().add(testRequester);
            getComments().add(new Chirick(){{
                setUser(testRequester);
            }});
            setParent(new Chirick() {{
                setId("y555hy4h545h454h");
                setUser(testRequester);
            }});
        }};

        this.testUser.getRechiricks().add(this.testChirick);
        this.testUser.getLikes().add(this.testChirick);

        List<Chirick> testChiricks = new ArrayList<>();
        testChiricks.add(testChirick);

        when(this.userService.getByHandleSimple(eq(this.testRequester.getHandle()))).thenReturn(new SimpleUserServiceModel());
        when(this.userService.getByHandleModel(eq(this.testUser.getHandle()))).thenReturn(new UserServiceModel());
        when(this.chirickRepository.findAllByUserHandleAndUserIsEnabledTrueAndParentIsNullOrderByDateDesc(this.testUser.getHandle(), this.pageable)).thenReturn(testChiricks);
        when(this.chirickRepository.getChiricksInCollectionAndUserIsEnabledTrue(any(), eq(this.pageable))).thenReturn(testChiricks);
        when(this.mapper.map(any(SimpleUserServiceModel.class), eq(User.class))).thenReturn(this.testRequester);
        when(this.mapper.map(any(UserServiceModel.class), eq(User.class))).thenReturn(this.testUser);
        when(this.mapper.map(any(Chirick.class), eq(ChirickViewModel.class))).thenAnswer(a -> {
            Chirick c = a.getArgument(0);
            ChirickViewModel m = new ChirickViewModel();
            m.setChirick(c.getChirick());
            m.setUserHandle(c.getUser().getHandle());
            m.setUserName(c.getUser().getName());
            m.setUserProfilePicUrl(c.getUser().getProfile().getProfilePicUrl());
            return m;
        });
    }

    @Test
    public void testGetActivity_WithValidData_ShouldNotThrowOrReturnNull() throws UserNotFoundException {
        List<ChirickViewModel> result = this.chirickService.getChiricksFromUser(this.testUser.getHandle(), this.testRequester.getHandle(), this.pageable);

        assertNotEquals("Result model should not be null.", result, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetActivity_WithInvalidUserHandle_ShouldThrow() throws UserNotFoundException {
        this.chirickService.getCommentsFromChirick(USER_HANDLE, "grwg4w44wg4gw", this.pageable);
    }

    @Test
    public void testGetChiricks_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        List<ChirickViewModel> resultList = this.chirickService.getChiricksFromUser(this.testUser.getHandle(), this.testRequester.getHandle(), this.pageable);

        ChirickViewModel chirickFromRequester = resultList.get(0);
        this.testMappedViewModel(chirickFromRequester);
    }

    @Test
    public void testGetComments_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        List<ChirickViewModel> resultList = this.chirickService.getChiricksFromUser(this.testUser.getHandle(), this.testRequester.getHandle(), this.pageable);

        ChirickViewModel chirickFromRequester = resultList.get(0);
        this.testMappedViewModel(chirickFromRequester);
        assertEquals("Comments size was not mapped correctly.", chirickFromRequester.getCommentsSize(), this.testChirick.getComments().size());
        assertTrue("Should be commented by requester.", chirickFromRequester.isCommented());
    }

    @Test
    public void testGetRechiricks_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        List<ChirickViewModel> resultList = this.chirickService.getRechiricksFromUser(this.testUser.getHandle(), this.testRequester.getHandle(), this.pageable);

        ChirickViewModel chirickFromRequester = resultList.get(0);
        this.testMappedViewModel(chirickFromRequester);
        assertEquals("Rechiricks size was not mapped correctly.", chirickFromRequester.getRechiricksSize(), this.testChirick.getRechiricks().size());
        assertTrue("Should be rechiricked by requester.", chirickFromRequester.isRechiricked());
    }

    @Test
    public void testGetLikes_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        List<ChirickViewModel> resultList = this.chirickService.getLikesFromUser(this.testUser.getHandle(), this.testRequester.getHandle(), this.pageable);

        ChirickViewModel chirickFromRequester = resultList.get(0);
        this.testMappedViewModel(chirickFromRequester);
        assertEquals("Likes size was not mapped correctly.", chirickFromRequester.getRechiricksSize(), this.testChirick.getRechiricks().size());
        assertTrue("Should be liked by requester.", chirickFromRequester.isLiked());
    }

    private void testMappedViewModel(ChirickViewModel model) {
        assertEquals("User handle was not mapped correctly.", model.getUserHandle(), this.testUser.getHandle());
        assertEquals("User name was not mapped correctly.", model.getUserName(), this.testUser.getName());
        assertEquals("User profile pic was not mapped correctly.", model.getUserProfilePicUrl(), this.testUser.getProfile().getProfilePicUrl());
        assertEquals("Chirick id was not mapped correctly.", model.getId(), this.testChirick.getId());
        assertEquals("Chirick was not mapped correctly.", model.getChirick(), this.testChirick.getChirick());
        assertTrue("Parent URL was not mapped correctly.", model.getParentUrl()
                .startsWith("/@" + this.testChirick
                        .getParent()
                        .getUser()
                        .getHandle()));

    }
}