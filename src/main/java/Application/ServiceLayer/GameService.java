package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Entities.Map;
import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;
import Application.Repositories.GameInstanceRepository;
import Application.Repositories.QuestionRepository;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameService {
    private static GameService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private GameInstanceRepository gameInstanceRepository;
    private QuestionRepository questionRepository;
    private java.util.Map<UUID, GameInstance> gameInstanceMap;
    private GameRunningService gameRunningService;

    private GameService(){}

    public static GameService getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new GameService();
        }
        return instance;
    }
    private List<Question> initQuestionsObjects(){
        List<Question> questionList = new LinkedList<>();
        List<Answer> answerList1 = new LinkedList<>();
        answerList1.add(new Answer("6", true));
        answerList1.add(new Answer("1", false));
        answerList1.add(new Answer("-5", false));
        Question question1 = new Question(false, "1 + 5 = ?", answerList1,1);
        questionRepository.save(question1);
        questionList.add(question1);
        // Question 2
        List<Answer> answerList2 = new LinkedList<>();
        answerList2.add(new Answer("France", false));
        answerList2.add(new Answer("Germany", false));
        answerList2.add(new Answer("Italy", true));
        Question question2 = new Question(false, "What is the capital of Italy?", answerList2, 2);
        questionRepository.save(question2);
        questionList.add(question2);

// Question 3
        List<Answer> answerList3 = new LinkedList<>();
        answerList3.add(new Answer("Earth", false));
        answerList3.add(new Answer("Mars", true));
        answerList3.add(new Answer("Venus", false));
        Question question3 = new Question(false, "Which planet is known as the Red Planet?", answerList3, 3);
        questionRepository.save(question3);
        questionList.add(question3);

// Question 4
        List<Answer> answerList4 = new LinkedList<>();
        answerList4.add(new Answer("Australia", true));
        answerList4.add(new Answer("Brazil", false));
        answerList4.add(new Answer("Canada", false));
        Question question4 = new Question(false, "Which country is also a continent?", answerList4, 4);
        questionRepository.save(question4);
        questionList.add(question4);

// Question 5
        List<Answer> answerList5 = new LinkedList<>();
        answerList5.add(new Answer("Leonardo da Vinci", true));
        answerList5.add(new Answer("Vincent van Gogh", false));
        answerList5.add(new Answer("Pablo Picasso", false));
        Question question5 = new Question(false, "Who painted the Mona Lisa?", answerList5, 5);
        questionRepository.save(question5);
        questionList.add(question5);

// Question 6
        List<Answer> answerList6 = new LinkedList<>();
        answerList6.add(new Answer("Brazil", false));
        answerList6.add(new Answer("Argentina", false));
        answerList6.add(new Answer("Chile", true));
        Question question6 = new Question(false, "Which country is located along the western coast of South America?", answerList6, 1);
        questionRepository.save(question6);
        questionList.add(question6);
// Question 7
        List<Answer> answerList7 = new LinkedList<>();
        answerList7.add(new Answer("Tokyo", true));
        answerList7.add(new Answer("Beijing", false));
        answerList7.add(new Answer("Seoul", false));
        Question question7 = new Question(false, "What is the capital of Japan?", answerList7, 3);
        questionRepository.save(question7);
        questionList.add(question7);

// Question 8
        List<Answer> answerList8 = new LinkedList<>();
        answerList8.add(new Answer("Eiffel Tower", false));
        answerList8.add(new Answer("Leaning Tower of Pisa", true));
        answerList8.add(new Answer("Big Ben", false));
        Question question8 = new Question(false, "Which famous landmark is located in Italy?", answerList8, 3);
        questionRepository.save(question8);
        questionList.add(question8);

// Question 9
        List<Answer> answerList9 = new LinkedList<>();
        answerList9.add(new Answer("Python", true));
        answerList9.add(new Answer("Ruby", false));
        answerList9.add(new Answer("Java", false));
        Question question9 = new Question(false, "Which programming language uses indentation for block delimiters?", answerList9, 4);
        questionRepository.save(question9);
        questionList.add(question9);

// Question 10
        List<Answer> answerList10 = new LinkedList<>();
        answerList10.add(new Answer("Amazon River", false));
        answerList10.add(new Answer("Nile River", true));
        answerList10.add(new Answer("Mississippi River", false));
        Question question10 = new Question(false, "Which river is the longest in the world?", answerList10, 1);
        questionRepository.save(question10);
        questionList.add(question10);

// Question 11
        List<Answer> answerList11 = new LinkedList<>();
        answerList11.add(new Answer("Russia", true));
        answerList11.add(new Answer("Canada", false));
        answerList11.add(new Answer("China", false));
        Question question11 = new Question(false, "Which country is the largest by land area?", answerList11, 1);
        questionRepository.save(question11);
        questionList.add(question11);

// Question 12
        List<Answer> answerList12 = new LinkedList<>();
        answerList12.add(new Answer("Mozart", false));
        answerList12.add(new Answer("Beethoven", true));
        answerList12.add(new Answer("Bach", false));
        Question question12 = new Question(false, "Who composed the Moonlight Sonata?", answerList12, 2);
        questionRepository.save(question12);
        questionList.add(question12);

// Question 13
        List<Answer> answerList13 = new LinkedList<>();
        answerList13.add(new Answer("New York City", false));
        answerList13.add(new Answer("Los Angeles", false));
        answerList13.add(new Answer("Chicago", true));
        Question question13 = new Question(false, "Which city is known as the Windy City?", answerList13, 3);
        questionRepository.save(question13);
        questionList.add(question13);

// Question 14
        List<Answer> answerList14 = new LinkedList<>();
        answerList14.add(new Answer("Shakespeare", true));
        answerList14.add(new Answer("Hemingway", false));
        answerList14.add(new Answer("Tolstoy", false));
        Question question14 = new Question(false, "Who wrote the play 'Romeo and Juliet'?", answerList14, 4);
        questionRepository.save(question14);
        questionList.add(question14);

// Question 15
        List<Answer> answerList15 = new LinkedList<>();
        answerList15.add(new Answer("Beijing", true));
        answerList15.add(new Answer("Shanghai", false));
        answerList15.add(new Answer("Guangzhou", false));
        Question question15 = new Question(false, "What is the capital of China?", answerList15, 5);
        questionRepository.save(question15);
        questionList.add(question15);

// Question 16
        List<Answer> answerList16 = new LinkedList<>();
        answerList16.add(new Answer("Michael Jordan", false));
        answerList16.add(new Answer("LeBron James", true));
        answerList16.add(new Answer("Kobe Bryant", false));
        Question question16 = new Question(false, "Who is widely regarded as the greatest basketball player of all time?", answerList16, 5);
        questionRepository.save(question16);
        questionList.add(question16);

        // Question 17
        List<Answer> answerList17 = new LinkedList<>();
        answerList17.add(new Answer("Great Wall of China", true));
        answerList17.add(new Answer("Taj Mahal", false));
        answerList17.add(new Answer("Machu Picchu", false));
        Question question17 = new Question(false, "Which is the longest wall in the world?", answerList17, 3);
        questionRepository.save(question17);
        questionList.add(question17);

// Question 18
        List<Answer> answerList18 = new LinkedList<>();
        answerList18.add(new Answer("Ganges", false));
        answerList18.add(new Answer("Nile", false));
        answerList18.add(new Answer("Amazon", true));
        Question question18 = new Question(false, "Which river has the largest drainage basin?", answerList18, 1);
        questionRepository.save(question18);
        questionList.add(question18);

// Question 19
        List<Answer> answerList19 = new LinkedList<>();
        answerList19.add(new Answer("Madrid", false));
        answerList19.add(new Answer("Barcelona", false));
        answerList19.add(new Answer("Seville", true));
        Question question19 = new Question(false, "Which city is the capital of Andalusia?", answerList19, 1);
        questionRepository.save(question19);
        questionList.add(question19);

// Question 20
        List<Answer> answerList20 = new LinkedList<>();
        answerList20.add(new Answer("Paris", false));
        answerList20.add(new Answer("London", true));
        answerList20.add(new Answer("Rome", false));
        Question question20 = new Question(false, "Which city lies on the River Thames?", answerList20, 2);
        questionRepository.save(question20);
        questionList.add(question20);

// Question 21
        List<Answer> answerList21 = new LinkedList<>();
        answerList21.add(new Answer("Neptune", true));
        answerList21.add(new Answer("Saturn", false));
        answerList21.add(new Answer("Mars", false));
        Question question21 = new Question(false, "Which planet is the farthest from the Sun?", answerList21, 1);
        questionRepository.save(question21);
        questionList.add(question21);

// Question 22
        List<Answer> answerList22 = new LinkedList<>();
        answerList22.add(new Answer("Berlin", true));
        answerList22.add(new Answer("Munich", false));
        answerList22.add(new Answer("Frankfurt", false));
        Question question22 = new Question(false, "Which city is the capital of Germany?", answerList22, 2);
        questionRepository.save(question22);
        questionList.add(question22);

// Question 23
        List<Answer> answerList23 = new LinkedList<>();
        answerList23.add(new Answer("Sydney", true));
        answerList23.add(new Answer("Melbourne", false));
        answerList23.add(new Answer("Brisbane", false));
        Question question23 = new Question(false, "Which city is home to the Sydney Opera House?", answerList23, 3);
        questionRepository.save(question23);
        questionList.add(question23);

// Question 24
        List<Answer> answerList24 = new LinkedList<>();
        answerList24.add(new Answer("William Shakespeare", true));
        answerList24.add(new Answer("Charles Dickens", false));
        answerList24.add(new Answer("Jane Austen", false));
        Question question24 = new Question(false, "Who wrote the play 'Hamlet'?", answerList24, 4);
        questionRepository.save(question24);
        questionList.add(question24);

// Question 25
        List<Answer> answerList25 = new LinkedList<>();
        answerList25.add(new Answer("India", true));
        answerList25.add(new Answer("China", false));
        answerList25.add(new Answer("Brazil", false));
        Question question25 = new Question(false, "Which country celebrates Diwali?", answerList25, 5);
        questionRepository.save(question25);
        questionList.add(question25);

// Question 26
        List<Answer> answerList26 = new LinkedList<>();
        answerList26.add(new Answer("Giraffe", true));
        answerList26.add(new Answer("Elephant", false));
        answerList26.add(new Answer("Lion", false));
        Question question26 = new Question(false, "Which animal has the longest neck?", answerList26, 2);
        questionRepository.save(question26);
        questionList.add(question26);

        return questionList;
    }
    private void initObjects(){
        // init objects:
        List<Question> questionList = initQuestionsObjects();





    }
    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
        this.gameInstanceMap = new HashMap<>();
        this.gameRunningService = GameRunningService.getInstance();
        if (!gameRunningService.isInit())
            gameRunningService.init(repositoryFactory);
        initObjects();

//        questionList.add(question1);
//        Question question2 = new Question(UUID.randomUUID(), false, "1 + 6 = ?", "7",new String[2], 1, new String[2]);
//        questionList.add(question2);
//        Question question3 = new Question(UUID.randomUUID(), false, "0 + 0 = ?", "0", new String[2], 5, new String[2]);
//        questionList.add(question3);
//        Question question4 = new Question(UUID.randomUUID(), false, "1 + 10 = ?", "11", new String[2], 2, new String[2]);
//        questionList.add(question4);
//        Question question5 = new Question(UUID.randomUUID(), false, "4 + 5 = ?", "9", new String[2], 2, new String[2]);
//        questionList.add(question5);
//        Question question6 = new Question(UUID.randomUUID(), false, "9 - 3 = ?", "6", new String[2], 5, new String[2]);
//        questionList.add(question6);
//        Question question7 = new Question(UUID.randomUUID(), false, "9 - 4 = ?", "5", new String[2], 4, new String[2]);
//        questionList.add(question6);
//        Question question8 = new Question(UUID.randomUUID(), false, "10 * 10 = ?", "100", new String[2], 4, new String[2]);
//        questionList.add(question6);
//        Question question9 = new Question(UUID.randomUUID(), false, "1 * 4 = ?", "4", new String[2], 2, new String[2]);
//        questionList.add(question7);
//        Question question10 = new Question(UUID.randomUUID(), false, "5 * 4 = ?", "20", new String[2], 5, new String[2]);
//        questionList.add(question8);
//        Questionnaire questionnaire = new Questionnaire(UUID.randomUUID(), "math", questionList);
//        Map map = new Map(UUID.randomUUID());
//        User host = repositoryFactory.userRepository.findAll().get(0);
//        GameInstance gameInstance = new GameInstance(UUID.randomUUID(), host, questionnaire, map, GameStatus.CREATED.toString(), 2, "first game", "this is a very good game!",
//                GroupAssignmentProtocol.RANDOM.toString(), 1000, true, 50);
//        this.gameInstanceMap.put(gameInstance.getId(), gameInstance);
//        System.out.println("game uuid " + gameInstance.getId());
//        System.out.println("host uuid " + host.getId());
//
//        RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
//        System.out.println("game code " + runningGameInstance.getCode());
//        this.gameRunningService.addRunningGame(runningGameInstance);
//        System.out.println("running game uuid " + runningGameInstance.getRunningId());
//

//        gameInstanceRepository.save(gameInstance);
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
        this.questionRepository = repositoryFactory.questionRepository;
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

//    public Response<FlatGameInstance> getGameInstance(UUID id){
//        return Response.ok(new FlatGameInstance(gameInstanceMap.get(id)));
//    }
    public Response<GameInstance> getGameInstance(UUID id){
        return Response.ok(gameInstanceMap.get(id));
    }
    public Response<Collection<FlatGameInstance>> getAllGameInstance(){
        Collection<FlatGameInstance> flatGames = new ConcurrentLinkedQueue<>();
        for (GameInstance gameInstance : gameInstanceMap.values())
            flatGames.add(new FlatGameInstance(gameInstance));
        return Response.ok(flatGames);
    }
}
