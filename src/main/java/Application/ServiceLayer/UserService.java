package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.DataAccessLayer.DALController;
import Application.Entities.users.User;
import Application.DataAccessLayer.Repositories.RepositoryFactory;
import Application.DataAccessLayer.Repositories.UserRepository;
import Application.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static JsonToInstance jsonToInstance;
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;
    private DALController dalController;
    private EventService eventService;

    @Autowired
    private UserService(RepositoryFactory repositoryFactory,
            UserRepository userRepository,
            EventService eventService) {
        this.repositoryFactory = repositoryFactory;
        this.userRepository = userRepository;
        this.dalController = DALController.getInstance();
        this.eventService = eventService;
    }

    public Response<User> register(JSONObject jsonObject) {
        try {
            Response<User> response = jsonToInstance.buildUser(jsonObject);
            if (response.isSuccessful()) {
                User user = response.getValue();
                userRepository.save(user);
            }
            return response;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }


    public Response<User> login(String username, String password) {
        try {
            List<User> users = userRepository.findByNameContaining(username);
            if(users.isEmpty())
                return Response.fail("user not exist");
            User user = users.get(0);
            if (user.getPassword().equals(password)) {
                eventService.addEmitter(user.getId());
                return Response.ok(user);
            }
            return Response.fail("password incorrect");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
}
