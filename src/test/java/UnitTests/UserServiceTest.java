package UnitTests;

import Application.APILayer.JsonToInstance;
import Application.DataAccessLayer.DALController;
import Application.Entities.User;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import Application.Response;
import Application.ServiceLayer.EventService;
import Application.ServiceLayer.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private RepositoryFactory repositoryFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DALController dalController;

    @Mock
    private EventService eventService;

    @Mock
    private JsonToInstance jsonToInstance;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService.init(repositoryFactory);
        userService.setDalController(dalController);
    }

    @Test
    public void testRegisterUser() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "testUser");
        jsonObject.put("password", "testPass");

        User mockUser = mock(User.class);
        Response<User> mockResponse = Response.ok(mockUser);

        when(jsonToInstance.buildUser(jsonObject)).thenReturn(mockResponse);

        Response<User> response = userService.register(jsonObject);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockUser, response.getValue(), "Expected response value to be the registered user");
        verify(userRepository, times(1)).save(mockUser);
    }


    @Test
    public void testRegisterUserWithInternalServerError() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "testUser");
        jsonObject.put("password", "testPass");

        when(jsonToInstance.buildUser(jsonObject)).thenThrow(new RuntimeException("Unexpected error"));

        Response<User> response = userService.register(jsonObject);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(500, response.getStatus(), "Expected status to be 500");
   }

    @Test
    public void testLoginUser() {
        String username = "testUser";
        String password = "testPass";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(password);

        when(userRepository.findByNameContaining(username)).thenReturn(Collections.singletonList(mockUser));

        Response<User> response = userService.login(username, password);

        assertTrue(response.isSuccessful(), response.getMessage());
        assertEquals(mockUser, response.getValue(), "Expected response value to be the logged in user");
        verify(eventService, times(1)).addEmitter(mockUser.getId());
    }

    @Test
    public void testLoginUserWithNonexistentUser() {
        String username = "nonexistentUser";
        String password = "testPass";

        when(userRepository.findByNameContaining(username)).thenReturn(Collections.emptyList());

        Response<User> response = userService.login(username, password);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
    }

    @Test
    public void testLoginUserWithIncorrectPassword() {
        String username = "testUser";
        String password = "wrongPass";
        String correctPassword = "testPass";

        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn(correctPassword);

        when(userRepository.findByNameContaining(username)).thenReturn(Collections.singletonList(mockUser));

        Response<User> response = userService.login(username, password);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
    }

    @Test
    public void testLoginUserWithInternalServerError() {
        String username = "testUser";
        String password = "testPass";

        when(userRepository.findByNameContaining(username)).thenThrow(new RuntimeException("Unexpected error"));

        Response<User> response = userService.login(username, password);

        assertFalse(response.isSuccessful(), "Expected response to be unsuccessful");
        assertEquals(500, response.getStatus(), "Expected status to be 500");
    }
}
