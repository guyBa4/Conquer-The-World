package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Repositories.QuestionRepository;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import Application.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static UserService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;

    private UserService() {
    }

    public static UserService getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new UserService();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
    }


    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.userRepository = repositoryFactory.userRepository;
    }

    public Response<User> register(JSONObject jsonObject) {
        try {
            Response<User> response = jsonToInstance.buildUser(jsonObject);
            if (response.isSuccessful()) {
                User user = response.getValue();
                userRepository.save(user);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }


    public Response<User> Login(String username, String password) {
        try {
            List<User> users = userRepository.findByNameContaining(username);
            if(users.isEmpty())
                return Response.fail("user not exist");
            User user = users.get(0);
            if (user.getPassword().equals(password))
                return Response.ok(user);
            return Response.fail("password incorrect");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
