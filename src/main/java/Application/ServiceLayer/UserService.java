package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import Application.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private RepositoryFactory repositoryFactory;
    private UserRepository userRepository;
    private JsonToInstance jsonToInstance;

    @Autowired
    public UserService(RepositoryFactory repositoryFactory){
        setRepositories(repositoryFactory);
        this.jsonToInstance = JsonToInstance.getInstance();
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


}
