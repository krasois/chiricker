package com.chiricker.services.chirick;

import com.chiricker.areas.chiricks.models.entities.Chirick;
import com.chiricker.areas.chiricks.models.service.ChirickServiceModel;
import com.chiricker.areas.chiricks.repositories.ChirickRepository;
import com.chiricker.areas.chiricks.services.chirick.ChirickServiceImpl;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChirickServiceGetByIdTests {

    @Mock
    private ChirickRepository chirickRepository;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ChirickServiceImpl chirickService;

    private Chirick testChirick;

    @Before
    public void setup() {
        User user = new User();
        user.setId("ersdrhgwrgsrg4whg4");
        user.setHandle("pesho");

        this.testChirick =  new Chirick();
        this.testChirick.setId("asdfwe4w43gw43gw");
        this.testChirick.setChirick("some chirick");
        this.testChirick.setUser(user);
        this.testChirick.setDate(new Date());
        this.testChirick.setParent(new Chirick() {{ setId("544gw4afa3af"); }});

        when(this.chirickRepository.findById(this.testChirick.getId())).thenReturn(Optional.of(this.testChirick));
        when(this.mapper.map(any(Chirick.class), eq(ChirickServiceModel.class))).thenAnswer(a -> {
            Chirick c = a.getArgument(0);
            ChirickServiceModel model = new ChirickServiceModel();
            model.setId(c.getId());
            model.setChirick(c.getChirick());
            model.setDate(c.getDate());
            model.setUser(new UserServiceModel() {{ setId(c.getUser().getId()); }});
            model.setParent(new ChirickServiceModel() {{ setId(c.getParent().getId()); }});

            return model;
        });
    }

    @Test
    public void testGetById_WithValidId_ShouldNotReturnNull() {
        ChirickServiceModel model = this.chirickService.getById(this.testChirick.getId());

        assertNotEquals("Result model should not be null.", model, null);
    }

    @Test
    public void testGetById_WithInvalidId_ShouldReturnNull() {
        ChirickServiceModel model = this.chirickService.getById("asawfafwaf");

        assertEquals("Result model should be null.", model, null);
    }

    @Test
    public void testGetById_WithValidId_ShouldMapCorrectly() {
        ChirickServiceModel model = this.chirickService.getById(this.testChirick.getId());

        assertEquals("Id is not mapped correctly.", model.getId(), this.testChirick.getId());
        assertEquals("Chirick is not mapped correctly.", model.getChirick(), this.testChirick.getChirick());
        assertEquals("Date is not mapped correctly.", model.getDate(), this.testChirick.getDate());
        assertEquals("Id is not mapped correctly.", model.getParent().getId(), this.testChirick.getParent().getId());
        assertEquals("Id is not mapped correctly.", model.getUser().getId(), this.testChirick.getUser().getId());
    }
}
