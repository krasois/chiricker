package com.chiricker.services.chirick;

import com.chiricker.areas.chiricks.models.binding.ChirickBindingModel;
import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChirickServiceAddTests {

    @Mock
    private ChirickRepository chirickRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ChirickServiceImpl chirickService;

    private UserServiceModel testUser;
    private ChirickBindingModel testModel;

    @Before
    public void setup() {
        this.testModel = new ChirickBindingModel();
        this.testModel.setChirick("some chriick");

        this.testUser = new UserServiceModel();
        this.testUser.setId("rgweaeg24wgw");
        this.testUser.setHandle("pesho");

        when(this.userService.getByHandle(this.testUser.getHandle())).thenReturn(this.testUser);
        when(this.chirickRepository.save(any(Chirick.class))).thenAnswer(a -> a.getArgument(0));
        when(this.mapper.map(any(ChirickBindingModel.class), eq(Chirick.class))).thenAnswer(a -> {
            ChirickBindingModel m = a.getArgument(0);
            Chirick c = new Chirick();
            c.setChirick(m.getChirick());
            return c;
        });
        when(this.mapper.map(any(UserServiceModel.class), eq(User.class))).thenAnswer(a -> {
            UserServiceModel m = a.getArgument(0);
            User user = new User();
            user.setHandle(m.getHandle());
            user.setId(m.getId());

            return user;
        });
        when(this.mapper.map(any(Chirick.class), eq(ChirickServiceModel.class))).thenAnswer(a -> {
            Chirick c = a.getArgument(0);
            ChirickServiceModel m = new ChirickServiceModel();
            m.setChirick(c.getChirick());
            m.setUser(new UserServiceModel() {{
                setId(c.getUser().getId());
                setHandle(c.getUser().getHandle());
            }});
            return m;
        });
    }

    @Test
    public void testAddChirick_WithValidData_ShouldNotReturnNullOrThrow() throws UserNotFoundException {
        ChirickServiceModel model = this.chirickService.add(this.testModel, this.testUser.getHandle());

        assertNotEquals("Model should not be null", model, null);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddChirick_WithInvalidHandle_ShouldThrow() throws UserNotFoundException {
        this.chirickService.add(this.testModel, "asdadasdasd");
    }

    @Test
    public void testAddChirick_WithValidData_ShouldMapCorrectly() throws UserNotFoundException {
        ChirickServiceModel model = this.chirickService.add(this.testModel, this.testUser.getHandle());

        assertEquals("Chirick is not mapped correctly.", model.getChirick(), this.testModel.getChirick());
        assertEquals("Chirick is not mapped correctly.", model.getUser().getId(), this.testUser.getId());
        assertEquals("Chirick is not mapped correctly.", model.getUser().getHandle(), this.testUser.getHandle());
    }
}