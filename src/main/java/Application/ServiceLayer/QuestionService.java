package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.APILayer.controllers.Requests.NewQuestion;
import Application.DataAccessLayer.DALController;
import Application.Entities.games.GameInstance;
import Application.Entities.questions.*;
import Application.Entities.users.User;
import Application.DataAccessLayer.Repositories.*;
import Application.Response;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private static QuestionService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private QuestionRepository questionRepository;
    private QuestionnaireRepository questionnaireRepository;
    private AnswerRepository answerRepository;
    private AssignedQuestionRepository assignedQuestionRepository;
    private GameInstanceRepository gameInstanceRepository;

    public QuestionService setDalController(DALController dalController) {
        this.dalController = dalController;
        return this;
    }

    private DALController dalController;

    public QuestionService() {
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
        if (dalController.needToInitiate())
            dalController.init(repositoryFactory);
        setRepositories(repositoryFactory);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        this.questionRepository = repositoryFactory.questionRepository;
        this.questionnaireRepository = repositoryFactory.questionnaireRepository;
        this.answerRepository = repositoryFactory.answerRepository;
        this.assignedQuestionRepository = repositoryFactory.assignedQuestionRepository;
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
    }


    public Response<Question> addQuestion(NewQuestion newQuestion) {
        try {
            byte[] image = null;
            if (newQuestion.getImage() != null)
                image = Base64.getDecoder().decode(newQuestion.getImage().getBytes(StandardCharsets.UTF_8));
            Question questionObj = new Question(newQuestion.isMultipleChoice(), newQuestion.getQuestion(),
                    newQuestion.getDifficulty(), image, newQuestion.getTags(), newQuestion.isShared(), newQuestion.getCreatorId());
            List<Answer> answers = buildAnswers(newQuestion.getCorrectAnswer(), newQuestion.getIncorrectAnswers(), questionObj);
            questionObj.setAnswers(answers);
            questionRepository.save(questionObj);
            return Response.ok(questionObj);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    private List<Answer> buildAnswers(String correctAnswer, List<String> incorrectAnswers, Question questionObj) {
        List<Answer> answers = new LinkedList<>();
        answers.add(new Answer(correctAnswer, true, questionObj));
        for (String incorrectAnswer : incorrectAnswers){
            if (incorrectAnswer == null || incorrectAnswer.isEmpty())
                continue;
            answers.add(new Answer(incorrectAnswer, false, questionObj));
        }
        return answers;
    }

    public Response<List<AssignedQuestion>> getQuestionsByQuestionnaireId(UUID questionnaireId) {
        try {
            Questionnaire questionnaire = dalController.getQuestionnaire(questionnaireId);
            List<AssignedQuestion> assignedQuestions = questionnaire.getQuestions();
            return Response.ok(assignedQuestions);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<Questionnaire> addQuestionnaire(String title, String creatorId, Map questionsIdsToDifficulty, List<Object> tagsObj) {
        try {
            Set<String> tags = tagsObj.stream().map(Object::toString).collect(Collectors.toSet());
            List<AssignedQuestion> assignedQuestions = assignQuestionListBuilder(questionsIdsToDifficulty);
            UUID creatorUuid = UUID.fromString(creatorId);
            User creator = dalController.getUser(creatorUuid);
            Questionnaire questionnaire = new Questionnaire(title, assignedQuestions, creator, tags);
            questionnaireRepository.save(questionnaire);
            return Response.ok(questionnaire);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    private List<AssignedQuestion> assignQuestionListBuilder(Map questionsIdsToDifficulty){
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

    public Response<Page<Question>> filterQuestions(int page, int size, String content, List<String> tags, Integer difficulty, String userId) {
        Page<Question> questionPage = questionRepository.findByFilters(content, difficulty, tags, UUID.fromString(userId), PageRequest.of(page, size));
        return Response.ok(questionPage);
    }
    
    
    public Response<Page<Questionnaire>> filterQuestionnaires(int page, int size, String name, List<String> tags,UUID creatorId) {
        Page<Questionnaire> questionnairesPage;
        if (name != null && creatorId != null)
            questionnairesPage = questionnaireRepository.findByNameLikeAndUserId(name, creatorId, PageRequest.of(page, size));
        else if (name != null)
            questionnairesPage = questionnaireRepository.findByNameLike(name, PageRequest.of(page, size));
        else if (creatorId != null)
            questionnairesPage = questionnaireRepository.findByUserId(creatorId, PageRequest.of(page, size));
        else
            questionnairesPage = questionnaireRepository.findBy(PageRequest.of(page, size));
        return Response.ok(questionnairesPage);
    }


    @Transactional
    public Response<Boolean> deleteQuestion(UUID id)
    {
        try {
            List<AssignedQuestion> assignedQuestions = assignedQuestionRepository.findByQuestionId(id);
            for (AssignedQuestion assignedQuestion : assignedQuestions) {
                assignedQuestion.setQuestion(null);
            }
            assignedQuestionRepository.saveAll(assignedQuestions);
            questionRepository.deleteById(id);
            return Response.ok(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    @Transactional
    public Response<Boolean> deleteQuestionnaire(UUID id)
    {
        try {
            List<AssignedQuestion> assignedQuestions = assignedQuestionRepository.findByQuestionnaireId(id);
            List<GameInstance> gameInstances = gameInstanceRepository.findByQuestionnaireId(id);
            this.assignedQuestionRepository.deleteAll(assignedQuestions);
            for (GameInstance gameInstance : gameInstances){
                gameInstance.setQuestionnaire(null);
            }
            this.gameInstanceRepository.saveAll(gameInstances);
            this.questionnaireRepository.deleteById(id);
            return Response.ok(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

}
