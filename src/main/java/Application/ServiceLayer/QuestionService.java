package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Repositories.*;
import Application.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private static QuestionService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private QuestionRepository questionRepository;

    private QuestionService() {
    }

    public static QuestionService getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new QuestionService();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.questionRepository = repositoryFactory.questionRepository;
    }


    public Response<Question> addQuestion(JSONObject jsonObject) {
        try {
            Response<Question> response = jsonToInstance.buildQuestion(jsonObject);
            if (response.isSuccessful()) {
                Question question = response.getValue();
                questionRepository.save(question);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
