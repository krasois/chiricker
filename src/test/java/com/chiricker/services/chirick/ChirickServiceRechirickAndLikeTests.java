package com.chiricker.services.chirick;

import com.chiricker.areas.chiricks.exceptions.ChirickNotFoundException;
import com.chiricker.areas.chiricks.models.binding.LikeBindingModel;
import com.chiricker.areas.chiricks.models.binding.RechirickBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.view.ChirickLikeResultViewModel;
import com.chiricker.areas.chiricks.models.view.RechirickResultViewModel;
import com.chiricker.areas.chiricks.repositories.ChirickRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickServiceImpl;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChirickServiceRechirickAndLikeTests {

    @Mock
    private ChirickRepository chirickRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ChirickServiceImpl chirickService;

    private User testUser;
    private RechirickBindingModel testRechirickModel;
    private LikeBindingModel testLikeModel;
    private Chirick testChirick;

    @Before
    public void setup() {
        this.testRechirickModel = new RechirickBindingModel();
        this.testRechirickModel.setId("rewgeg3eawfw33");

        this.testLikeModel = new LikeBindingModel();
        this.testLikeModel.setId("rewgeg3eawfw33");

        this.testUser = new User();
        this.testUser.setId("rgweaeg24wgw");
        this.testUser.setHandle("pesho");

        this.testChirick = new Chirick();
        this.testChirick.setChirick("asdasawawda wadwad a");
        this.testChirick.setId("rewgeg3eawfw33");
        this.testChirick.setRechiricks(new HashSet<>());
        this.testChirick.getRechiricks().add(new User() {{ setId("sdfgtrht4w3wggw4"); }});
        this.testChirick.setLikes(new HashSet<>());
        this.testChirick.getLikes().add(new User() {{ setId("43wg43qwstet4h5eatsg"); }});

        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(this.testUser);
        when(this.chirickRepository.findById(this.testRechirickModel.getId())).thenReturn(Optional.of(this.testChirick));
    }

    @Test
    public void testRechirick_WithValiData_ShouldNotReturnNullOrThrow() throws UserNotFoundException, ChirickNotFoundException {
        RechirickResultViewModel model = this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());

        assertNotEquals("Rechirick result model should not be null.", model, null);
    }

    @Test
    public void testLike_WithValiData_ShouldNotReturnNullOrThrow() throws UserNotFoundException, ChirickNotFoundException {
        ChirickLikeResultViewModel model = this.chirickService.like(this.testLikeModel, this.testUser.getHandle());

        assertNotEquals("Like result model should not be null.", model, null);
    }

    @Test(expected = ChirickNotFoundException.class)
    public void testRechirick_WithInvalidChirickId_ShouldThrowChirickNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.testRechirickModel.setId("43asfas3f33");
        this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());
    }

    @Test(expected = ChirickNotFoundException.class)
    public void testLike_WithInvalidChirickId_ShouldThrowChirickNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.testLikeModel.setId("43asfas3f33");
        this.chirickService.like(this.testLikeModel, this.testUser.getHandle());
    }

    @Test(expected = UserNotFoundException.class)
    public void testRechirick_WithInvalidHandle_ShouldThrowUserNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.chirickService.rechirick(this.testRechirickModel, "asdasdasd");
    }

    @Test(expected = UserNotFoundException.class)
    public void testLike_WithInvalidHandle_ShouldThrowUserNotFound() throws UserNotFoundException, ChirickNotFoundException {
        this.chirickService.like(this.testLikeModel, "asdasdasd");
    }

    @Test
    public void testRechirick_WithValidData_ShouldRechirick() throws UserNotFoundException, ChirickNotFoundException {
        RechirickResultViewModel model = this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());

        assertTrue("Rechirick was not successfull.", model.getRechiricksSize() == 2);
    }

    @Test
    public void testLike_WithValidData_ShouldLike() throws UserNotFoundException, ChirickNotFoundException {
        ChirickLikeResultViewModel model = this.chirickService.like(this.testLikeModel, this.testUser.getHandle());

        assertTrue("Like was not successfull.", model.getLikesSize() == 2);
    }

    @Test
    public void testRechirick_WithValidData_ShouldUnrechirick() throws UserNotFoundException, ChirickNotFoundException {
        this.testChirick.getRechiricks().add(new User() {{ setId(testUser.getId()); }});
        RechirickResultViewModel model = this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());

        assertTrue("Rechirick was not successfull.", model.getRechiricksSize() == 1);
    }

    @Test
    public void testLike_WithValidData_ShouldUnlike() throws UserNotFoundException, ChirickNotFoundException {
        this.testChirick.getLikes().add(new User() {{ setId(testUser.getId()); }});
        ChirickLikeResultViewModel model = this.chirickService.like(this.testLikeModel, this.testUser.getHandle());

        assertTrue("Like was not successfull.", model.getLikesSize() == 1);
    }

    @Test
    public void testRechirick_WithValidData_ShouldMapCorrectly() throws UserNotFoundException, ChirickNotFoundException {
        RechirickResultViewModel model = this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());

        assertEquals("Chirick ID for rechirick did not map correctly.", model.getId(), this.testRechirickModel.getId());
    }

    @Test
    public void testLike_WithValidData_ShouldMapCorrectly() throws UserNotFoundException, ChirickNotFoundException {
        RechirickResultViewModel model = this.chirickService.rechirick(this.testRechirickModel, this.testUser.getHandle());

        assertEquals("Chirick ID for like did not map correctly.", model.getId(), this.testLikeModel.getId());
    }
}