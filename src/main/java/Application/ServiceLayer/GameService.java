package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.GameInstance;
import Application.Entities.Question;
import Application.Repositories.GameInstanceRepository;
import Application.Repositories.QuestionRepository;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class GameService {
    private static GameService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private GameInstanceRepository gameInstanceRepository;

    private GameService(){}

    public static GameService getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new GameService();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
    }

    public Response<GameInstance> addGameInstance(JSONObject jsonObject) {
        try {
//            Response<GameInstance> response = jsonToInstance.buildGameInstance(jsonObject);
            Response<GameInstance> response = GameInstance.fromJson(jsonObject);
            if (response.isSuccessful()) {
                GameInstance gameInstance = response.getValue();
                gameInstanceRepository.save(gameInstance);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
