package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.DataAccessLayer.DALController;
import Application.Entities.*;
import Application.Repositories.*;
import Application.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map;

@Service
public class QuestionService {
    private static QuestionService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private QuestionRepository questionRepository;
    private QuestionnaireRepository questionnaireRepository;
    private DALController dalController;

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
        this.dalController = DALController.getInstance();
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        this.questionRepository = repositoryFactory.questionRepository;
        this.questionnaireRepository = repositoryFactory.questionnaireRepository;
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

    public Response<List<AssignedQuestion>> getQuestionsByQuestionnaireId(UUID questionnaireId) {
        try {
            Questionnaire questionnaire = dalController.getQuestionnaire(questionnaireId);
            List<AssignedQuestion> assignedQuestions = questionnaire.getQuestions();
            return Response.ok(assignedQuestions);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<Questionnaire> addQuestionnaire(String title, String creatorId, Map questionsIdsToDifficulty) {
        try {
            List<AssignedQuestion> assignedQuestions = AssignQuestionListBuilder(questionsIdsToDifficulty);
            UUID creatorUuid = UUID.fromString(creatorId);
            User creator = dalController.getUser(creatorUuid);
            Questionnaire questionnaire = new Questionnaire(title, assignedQuestions, creator);
            questionnaireRepository.save(questionnaire);
            return Response.ok(questionnaire);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    private List<AssignedQuestion> AssignQuestionListBuilder(Map questionsIdsToDifficulty){
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();
        for (Object questionId: questionsIdsToDifficulty.keySet() ){
            Integer difficulity = (Integer) questionsIdsToDifficulty.get(questionId);
            UUID questionUuid = UUID.fromString((String) questionId);
            Question question = dalController.getQuestion(questionUuid);
            if (difficulity == 0)
                assignedQuestions.add(new AssignedQuestion(question));
            else
                assignedQuestions.add(new AssignedQuestion(question, difficulity));
        }
        return assignedQuestions;
    }
}
