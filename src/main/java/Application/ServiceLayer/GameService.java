package Application.ServiceLayer;
import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Entities.Map;
import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;
import Application.Enums.TileType;
import Application.Repositories.*;
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
    private UserRepository userRepository;
    private QuestionnaireRepository questionnaireRepository;
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
    private void initObjects(){
        // init objects:

        User guyUser = new User("guy", "1234");
        userRepository.save(guyUser);
        User nitzanUser = new User("nitzan", "5555");
        userRepository.save(nitzanUser);
        List<Question> questions = initQuestionsObjects();
        List<AssignedQuestion> assignedQuestions = new LinkedList<>();
        for (Question question : questions){
            AssignedQuestion assignedQuestion = new AssignedQuestion(question);
            assignedQuestions.add(assignedQuestion);
        }
        Questionnaire questionnaire = new Questionnaire("common knolage questionnaire", assignedQuestions, guyUser);
        this.questionnaireRepository.save(questionnaire);
        List<Tile> tiles = init_tiles();
        Map map = new Map("USA", tiles, true);
        List<String> startingPossitions = new LinkedList<>();
        startingPossitions.add("TX");
        startingPossitions.add("NM");
        startingPossitions.add("ID");
        startingPossitions.add("SD");
        GameInstance gameInstance = new GameInstance (nitzanUser, questionnaire, null, GameStatus.CREATED.toString(), 3, "for fun",
                "the first game instance", GroupAssignmentProtocol.RANDOM.toString(), 1000 , true, 30, startingPossitions);
        gameInstanceRepository.save(gameInstance);
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
        this.questionnaireRepository =  repositoryFactory.questionnaireRepository;
        this.userRepository = repositoryFactory.userRepository;
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
//
//// Question 3
//        List<Answer> answerList3 = new LinkedList<>();
//        answerList3.add(new Answer("Earth", false));
//        answerList3.add(new Answer("Mars", true));
//        answerList3.add(new Answer("Venus", false));
//        Question question3 = new Question(false, "Which planet is known as the Red Planet?", answerList3, 3);
//        questionRepository.save(question3);
//        questionList.add(question3);
//
//// Question 4
//        List<Answer> answerList4 = new LinkedList<>();
//        answerList4.add(new Answer("Australia", true));
//        answerList4.add(new Answer("Brazil", false));
//        answerList4.add(new Answer("Canada", false));
//        Question question4 = new Question(false, "Which country is also a continent?", answerList4, 4);
//        questionRepository.save(question4);
//        questionList.add(question4);
//
//// Question 5
//        List<Answer> answerList5 = new LinkedList<>();
//        answerList5.add(new Answer("Leonardo da Vinci", true));
//        answerList5.add(new Answer("Vincent van Gogh", false));
//        answerList5.add(new Answer("Pablo Picasso", false));
//        Question question5 = new Question(false, "Who painted the Mona Lisa?", answerList5, 5);
//        questionRepository.save(question5);
//        questionList.add(question5);
//
//// Question 6
//        List<Answer> answerList6 = new LinkedList<>();
//        answerList6.add(new Answer("Brazil", false));
//        answerList6.add(new Answer("Argentina", false));
//        answerList6.add(new Answer("Chile", true));
//        Question question6 = new Question(false, "Which country is located along the western coast of South America?", answerList6, 1);
//        questionRepository.save(question6);
//        questionList.add(question6);
//// Question 7
//        List<Answer> answerList7 = new LinkedList<>();
//        answerList7.add(new Answer("Tokyo", true));
//        answerList7.add(new Answer("Beijing", false));
//        answerList7.add(new Answer("Seoul", false));
//        Question question7 = new Question(false, "What is the capital of Japan?", answerList7, 3);
//        questionRepository.save(question7);
//        questionList.add(question7);
//
//// Question 8
//        List<Answer> answerList8 = new LinkedList<>();
//        answerList8.add(new Answer("Eiffel Tower", false));
//        answerList8.add(new Answer("Leaning Tower of Pisa", true));
//        answerList8.add(new Answer("Big Ben", false));
//        Question question8 = new Question(false, "Which famous landmark is located in Italy?", answerList8, 3);
//        questionRepository.save(question8);
//        questionList.add(question8);
//
//// Question 9
//        List<Answer> answerList9 = new LinkedList<>();
//        answerList9.add(new Answer("Python", true));
//        answerList9.add(new Answer("Ruby", false));
//        answerList9.add(new Answer("Java", false));
//        Question question9 = new Question(false, "Which programming language uses indentation for block delimiters?", answerList9, 4);
//        questionRepository.save(question9);
//        questionList.add(question9);
//
//// Question 10
//        List<Answer> answerList10 = new LinkedList<>();
//        answerList10.add(new Answer("Amazon River", false));
//        answerList10.add(new Answer("Nile River", true));
//        answerList10.add(new Answer("Mississippi River", false));
//        Question question10 = new Question(false, "Which river is the longest in the world?", answerList10, 1);
//        questionRepository.save(question10);
//        questionList.add(question10);
//
//// Question 11
//        List<Answer> answerList11 = new LinkedList<>();
//        answerList11.add(new Answer("Russia", true));
//        answerList11.add(new Answer("Canada", false));
//        answerList11.add(new Answer("China", false));
//        Question question11 = new Question(false, "Which country is the largest by land area?", answerList11, 1);
//        questionRepository.save(question11);
//        questionList.add(question11);
//
//// Question 12
//        List<Answer> answerList12 = new LinkedList<>();
//        answerList12.add(new Answer("Mozart", false));
//        answerList12.add(new Answer("Beethoven", true));
//        answerList12.add(new Answer("Bach", false));
//        Question question12 = new Question(false, "Who composed the Moonlight Sonata?", answerList12, 2);
//        questionRepository.save(question12);
//        questionList.add(question12);
//
//// Question 13
//        List<Answer> answerList13 = new LinkedList<>();
//        answerList13.add(new Answer("New York City", false));
//        answerList13.add(new Answer("Los Angeles", false));
//        answerList13.add(new Answer("Chicago", true));
//        Question question13 = new Question(false, "Which city is known as the Windy City?", answerList13, 3);
//        questionRepository.save(question13);
//        questionList.add(question13);
//
//// Question 14
//        List<Answer> answerList14 = new LinkedList<>();
//        answerList14.add(new Answer("Shakespeare", true));
//        answerList14.add(new Answer("Hemingway", false));
//        answerList14.add(new Answer("Tolstoy", false));
//        Question question14 = new Question(false, "Who wrote the play 'Romeo and Juliet'?", answerList14, 4);
//        questionRepository.save(question14);
//        questionList.add(question14);
//
//// Question 15
//        List<Answer> answerList15 = new LinkedList<>();
//        answerList15.add(new Answer("Beijing", true));
//        answerList15.add(new Answer("Shanghai", false));
//        answerList15.add(new Answer("Guangzhou", false));
//        Question question15 = new Question(false, "What is the capital of China?", answerList15, 5);
//        questionRepository.save(question15);
//        questionList.add(question15);
//
//// Question 16
//        List<Answer> answerList16 = new LinkedList<>();
//        answerList16.add(new Answer("Michael Jordan", false));
//        answerList16.add(new Answer("LeBron James", true));
//        answerList16.add(new Answer("Kobe Bryant", false));
//        Question question16 = new Question(false, "Who is widely regarded as the greatest basketball player of all time?", answerList16, 5);
//        questionRepository.save(question16);
//        questionList.add(question16);
//
//        // Question 17
//        List<Answer> answerList17 = new LinkedList<>();
//        answerList17.add(new Answer("Great Wall of China", true));
//        answerList17.add(new Answer("Taj Mahal", false));
//        answerList17.add(new Answer("Machu Picchu", false));
//        Question question17 = new Question(false, "Which is the longest wall in the world?", answerList17, 3);
//        questionRepository.save(question17);
//        questionList.add(question17);
//
//// Question 18
//        List<Answer> answerList18 = new LinkedList<>();
//        answerList18.add(new Answer("Ganges", false));
//        answerList18.add(new Answer("Nile", false));
//        answerList18.add(new Answer("Amazon", true));
//        Question question18 = new Question(false, "Which river has the largest drainage basin?", answerList18, 1);
//        questionRepository.save(question18);
//        questionList.add(question18);
//
//// Question 19
//        List<Answer> answerList19 = new LinkedList<>();
//        answerList19.add(new Answer("Madrid", false));
//        answerList19.add(new Answer("Barcelona", false));
//        answerList19.add(new Answer("Seville", true));
//        Question question19 = new Question(false, "Which city is the capital of Andalusia?", answerList19, 1);
//        questionRepository.save(question19);
//        questionList.add(question19);
//
//// Question 20
//        List<Answer> answerList20 = new LinkedList<>();
//        answerList20.add(new Answer("Paris", false));
//        answerList20.add(new Answer("London", true));
//        answerList20.add(new Answer("Rome", false));
//        Question question20 = new Question(false, "Which city lies on the River Thames?", answerList20, 2);
//        questionRepository.save(question20);
//        questionList.add(question20);
//
//// Question 21
//        List<Answer> answerList21 = new LinkedList<>();
//        answerList21.add(new Answer("Neptune", true));
//        answerList21.add(new Answer("Saturn", false));
//        answerList21.add(new Answer("Mars", false));
//        Question question21 = new Question(false, "Which planet is the farthest from the Sun?", answerList21, 1);
//        questionRepository.save(question21);
//        questionList.add(question21);
//
//// Question 22
//        List<Answer> answerList22 = new LinkedList<>();
//        answerList22.add(new Answer("Berlin", true));
//        answerList22.add(new Answer("Munich", false));
//        answerList22.add(new Answer("Frankfurt", false));
//        Question question22 = new Question(false, "Which city is the capital of Germany?", answerList22, 2);
//        questionRepository.save(question22);
//        questionList.add(question22);
//
//// Question 23
//        List<Answer> answerList23 = new LinkedList<>();
//        answerList23.add(new Answer("Sydney", true));
//        answerList23.add(new Answer("Melbourne", false));
//        answerList23.add(new Answer("Brisbane", false));
//        Question question23 = new Question(false, "Which city is home to the Sydney Opera House?", answerList23, 3);
//        questionRepository.save(question23);
//        questionList.add(question23);
//
//// Question 24
//        List<Answer> answerList24 = new LinkedList<>();
//        answerList24.add(new Answer("William Shakespeare", true));
//        answerList24.add(new Answer("Charles Dickens", false));
//        answerList24.add(new Answer("Jane Austen", false));
//        Question question24 = new Question(false, "Who wrote the play 'Hamlet'?", answerList24, 4);
//        questionRepository.save(question24);
//        questionList.add(question24);
//
//// Question 25
//        List<Answer> answerList25 = new LinkedList<>();
//        answerList25.add(new Answer("India", true));
//        answerList25.add(new Answer("China", false));
//        answerList25.add(new Answer("Brazil", false));
//        Question question25 = new Question(false, "Which country celebrates Diwali?", answerList25, 5);
//        questionRepository.save(question25);
//        questionList.add(question25);
//
//// Question 26
//        List<Answer> answerList26 = new LinkedList<>();
//        answerList26.add(new Answer("Giraffe", true));
//        answerList26.add(new Answer("Elephant", false));
//        answerList26.add(new Answer("Lion", false));
//        Question question26 = new Question(false, "Which animal has the longest neck?", answerList26, 2);
//        questionRepository.save(question26);
//        questionList.add(question26);

        return questionList;
    }
    private List<Tile> init_tiles() {
        List<Tile> tiles = new LinkedList<>();
        tiles.add(new Tile("TX", TileType.FREE, 0, 2, "M573.98 536.935l-4.659-17.568v3.3l4.66 14.268zm-1.068-30.866l-3.3 6.212-.388 2.039 3.688-8.25zm2.621-4.562l3.106-4.076-2.232 1.359-.874 2.717zm10.968-9.9l-7.28 3.785 2.039-.388 5.241-3.397zm.97-1.359l2.04-1.65-.777.194-1.262 1.456zM472.356 327.376l-.68-.097-3.3 91.045-19.218-.873-30.769-1.747-12.327-.874 2.427 4.756.097.097 10.482 10.483 6.018 7.086 7.96 5.92 5.63 10.29 2.135 10.773 4.562 3.01 3.59 3.979 5.145 1.844 7.474 4.756 3.397 1.262 4.66-4.66 1.455-4.95 2.427-4.755 7.57-3.01 2.913 1.457 8.444.679 7.668 4.756 5.242.97-1.456 2.718 3.009 1.942 2.814 3.397.583 3.591 1.844 2.427 4.27 10.289 4.272 3.59 3.3 5.436 4.173 4.271 1.845.583 1.553 11.065 4.562 2.523.194 3.495 1.164-.292 7.96 9.707 3.98.873 5.047 3.01h7.765l5.63 3.105 6.697-1.456-3.01-2.814-2.717-8.154-1.068-7.085-1.553-2.33.68-4.756-2.039-.194-2.718-4.465 3.107 3.3 3.59-1.262 1.942-5.144-3.882-5.241 5.823.679 2.718-4.174-.388-1.456 2.426-3.203-.582 2.815 2.718-2.135-1.068-3.786 3.494 1.844 4.465-2.523-4.368-4.562 2.718 1.068 5.048.29 6.6-1.455 7.377-4.95 4.465-3.689.679-2.718 2.718-2.814-1.456-4.66.291-2.911 4.853-2.136-.388 5.145h2.912l-3.203 2.717 12.91-6.794 2.134-.097 1.36-6.212h.388l1.456-3.592.679-11.065.874-4.465-2.524-7.959-3.106-5.435-.097-2.621-3.203-4.465-1.942-18.733-.097-2.33-.194-2.426-.582-8.542-5.824.194-1.456-1.553-.291-.097h-.097l-3.786-.68-4.367-2.814-4.854-2.038-9.124 2.62-2.426-.582-3.01 1.165-3.687 3.009-9.9-4.66-3.107 4.271-4.562-1.65-4.853-3.106-2.62 2.233-2.524.097v-1.844l-6.795-3.203-2.718 1.261-1.358-1.553-7.474-.776-5.727-5.241-.874 1.747-4.173-.194-3.883-3.689-1.747-5.824-.291-33.195-24.848-.291-23.49-.583h-.582z"));
        tiles.add(new Tile("MT", TileType.FREE, 0, 2, "M465.658 72.974l-32.128-2.136-23.392-2.232-23.296-2.718-14.462-1.941-23.101-3.591-11.55-1.942-25.82-4.95-2.717-.582-4.174 19.8 1.844 3.592 1.456 5.533-.873.776 1.844 5.047 2.524 2.136 7.182 12.036.68 2.038 2.912-.194-3.398 15.918-1.358.388-1.845 5.339 9.61.68.776 4.464 5.436 13.298.97.776 3.786 7.765.97-.776 8.057-.097 10.191 1.067 2.524-3.3 3.009 5.436.582.291.097-.582 1.36-9.513 33.195 4.077 26.498 2.524 13.977 1.165 24.945 1.455 1.262.098h.291l.097-1.36.292-6.697.388-8.832.097-2.233.194-3.882 1.553-30.478 1.262-23.684.097-3.882-1.844-.097z"));
        tiles.add(new Tile("ND", TileType.FREE, 0, 2, "M556.509 73.847l-29.313.583-20.48-.097-17.569-.389-20.577-.776-1.068-.097-.097 3.882-1.262 23.684-1.553 30.478-.194 3.882 3.785.194 42.805 1.165 39.505-.291 16.404-.486 3.3-.194-.389-1.456-.485-4.464-2.232-5.242-.583-8.736-1.941-9.9-4.077-24.848-1.94-6.892h-2.04z"));
        tiles.add(new Tile("ID", TileType.FREE, 0, 1, "M309.095 52.882l-8.639-1.747-2.815-.68-1.261-.291-11.26 50.667.486 4.174-.389 4.367.195.971v.097l.097 1.844 3.3 3.592-4.756 11.938-10.095 13.201-.291 2.427 2.62 6.212-9.22 38.243-.389 2.038 2.718.582 27.469 5.436 10.968 2.038 2.815.485 2.718.486 13.977 2.232 25.042 3.786 2.815.388.388-3.203.874-6.31 3.3-25.236 1.747-12.52.388-3.204-.582-.291-3.01-5.436-2.523 3.3-10.191-1.067-8.057.097-.97.776-3.786-7.765-.97-.776-5.436-13.298-.776-4.465-9.61-.68 1.845-5.338 1.358-.388 3.398-15.918-2.912.194-.68-2.038-7.182-12.036-2.524-2.136-1.844-5.047.873-.776-1.456-5.533-1.844-3.591 4.174-19.801h-.097z"));
        tiles.add(new Tile("WA", TileType.FREE, 0, 2, "M192.134 77.05l.097-1.65-.194.097.097 1.553zm20.674-8.832l.583-.97-.68-.098.097 1.068zm-1.844-1.747l.582-1.942-.582-.194v2.136zm7.086-2.815l-.097-1.942-1.65 2.524 1.747-.582zm.194-4.077l.291-1.941-.194-.486-.097 2.427zm.291-9.512l-.097-1.068-.388-.097.485 1.165zm3.106-6.406l-.874-.097-1.747 2.717 2.621-2.62zm-2.427-2.621l.486-.388.097-.291-.583.679zm-.485.291l-.291-1.941-.874 1.65 1.165.291zm-.485-2.718l-.874-.388.68.97.194-.582zm2.912 1.65v-1.261l-.291.485.29.777zm.873-1.261l-.388-.292h-.097l.485.292zm-5.532-2.136l-.486-.388-.194-.097.68.485zm3.591-.388l-2.62.68.679.194 1.941-.874zm-2.135-.194l-.486-.582-.097.097.583.485zm2.038-1.165h-.388l.485.194-.097-.194zm-19.024 49.794l3.494 3.785-.583 7.28.68 1.844 5.241 2.815 14.754 1.359.582 1.747 6.989.097 6.6.873 10.774-1.164 5.339.873 1.456-.68 27.274 6.213 1.845.388-.195-.97.389-4.368-.486-4.174 11.26-50.667-1.553-.291-22.713-5.339-19.801-5.047-22.422-6.212-6.697-1.65 1.553 8.444-1.456 2.718-2.62-1.359 2.523 11.745-2.718 3.882-1.262 3.592v3.688l-5.726 3.98-2.427-1.553-2.33 1.165 4.271-5.533-1.067 3.785 2.426-2.62 1.942.388-.583-4.853 1.262.194 2.62-5.824-.873-.873-3.397 4.367-2.233.097 2.427-2.717 2.718-.68-.97-4.95-2.719-.68-1.358-2.329-1.65.874-8.93-3.883-7.862-6.115-2.136 5.921-.194 2.524 1.65 4.465-1.068 14.462-.776 2.232 4.368.389-4.368 3.106 2.33 1.164-1.845 6.212-.873-4.853-1.65 5.63 2.232 2.718 2.427-.194 2.912 1.747 1.455 2.815h1.748z"));
        tiles.add(new Tile("AZ", TileType.FREE, 0, 5, "M372.186 309.905l-32.71-4.077-17.569-2.523-25.042-3.98-2.524-.388v.194l-4.27 18.83-2.233-.097-3.397-3.009-4.27 3.591-.098 15.433-1.553 3.3v.389l-.194 2.426 3.397 9.124 3.009 4.077-5.436 3.203-2.135 3.98-3.494 8.541-1.747.388-.097 7.086 3.009 2.233-1.553 4.27-3.495.194-2.038 3.786 6.988 4.368 13.201 7.377 40.767 21.645 34.554 4.27h.388l2.718-28.633.68-7.086 7.28-75.127.387-3.494-2.523-.291z"));
        tiles.add(new Tile("CA", TileType.FREE, 0, 4, "M206.014 363.775l.388 2.135v.389l-.388-2.524zm-14.268-8.348l.679 1.456.388-.097-1.067-1.359zm16.986-.29l.29 2.523 1.748 1.262-2.038-3.786zm-11.745-13.784l-1.068-.29.874.387.194-.097zm-10.774-2.912l-2.62 1.553.679.68 1.941-2.233zm-5.047-1.844l-.194.777.873.194-.68-.97zm8.541 1.941l4.271 2.427.777-.68-5.048-1.747zm-33.098-85.512l-.097.097.097-.097zm10.385.485l-.097-.291v.388l.097-.097zM223 179.841l-3.786-.971-41.543-11.454-15.433-4.659v.388l-.873 9.61-4.077 11.356-6.794 8.445-.194 4.27 3.3 5.921 1.65 7.765-2.33 6.31.389 6.308-1.165 2.621 3.98 8.639 2.523 3.3-.194 9.415 6.018 5.727 3.494-5.824 3.786 3.009 1.456-1.359-5.242.97-.097 4.95 1.068 6.213-2.912-2.815-1.65-3.785-.486 5.241 1.36 10.386 5.435 6.697-3.591 4.66.582 5.92 3.882 6.018 6.795 18.636 2.135 1.165-.68 12.91 1.068 1.552 14.56 5.242 4.465 4.756 1.553 3.106 4.27 2.426 4.66.874 1.94 5.338 3.883 1.845 5.824 6.891 5.436 9.027-.388 8.347 3.008 3.98 39.7 3.882 3.494-.194 1.553-4.27-3.01-2.233.098-7.086 1.747-.388 3.494-8.541 2.136-3.98 5.435-3.203-3.009-4.077-3.397-9.124.194-2.426v-.097l-2.524-3.397-7.765-10.386-15.044-21.16-20.966-30.09-11.842-16.403-11.453-16.5 13.2-56.006.874-3.689z"));
        tiles.add(new Tile("CO", TileType.FREE, 0, 1, "M489.632 255.55l.389-19.025-4.66-.194-23.683-.874-2.33-.097-1.94-.097-28.828-1.844-19.219-1.553-23.974-2.33-2.33-.29-.291 2.523-5.242 50.57-1.844 17.665-.776 7.668-.194 2.524 3.009.291 48.92 4.27 21.547 1.36 21.451.97 3.106.098 1.942.097 4.076.097 1.941.097 6.115.194h1.553v-1.941l1.068-51.347.097-5.726v-1.845l.097-1.261z"));
        tiles.add(new Tile("NV", TileType.FREE, 0, 2, "M294.827 295.83l10.094-62.8 5.145-31.448.582-3.106-2.815-.485-10.968-2.038-27.469-5.436-2.718-.582-2.717-.583-13.686-3.008-24.557-5.824-2.718-.68-.874 3.689-13.2 56.005 11.453 16.5 11.842 16.404 20.966 30.09 15.044 21.16 7.765 10.386 2.524 3.397v-.291l1.553-3.3.097-15.434 4.27-3.59 3.398 3.008 2.233.097 4.27-18.83v-.194l.486-3.106z"));
        tiles.add(new Tile("NM", TileType.FREE, 0, 4, "M472.452 324.756l.097-2.524.097-2.524.097-2.523-3.106-.098-21.45-.97-21.549-1.359-48.92-4.27-3.008-.292-.389 3.494-7.28 75.127-.679 7.086-2.718 28.633.389.097 13.394 1.262 1.262-6.794 2.427-1.941 27.178 2.426h.194l-2.427-4.756 12.327.874 30.77 1.747 19.218.873 3.3-91.045.68.097.096-2.62z"));
        tiles.add(new Tile("OR", TileType.FREE, 0, 2, "M162.238 162.757l15.433 4.66 41.543 11.453 3.786.97 2.718.68 24.557 5.824 13.686 3.008 2.717.583.389-2.038 9.22-38.243-2.62-6.212.291-2.427 10.095-13.2 4.756-11.94-3.3-3.59-.098-1.845v-.097l-1.844-.388-27.274-6.212-1.456.68-5.339-.874-10.774 1.164-6.6-.873-6.989-.097-.582-1.747-14.754-1.36-5.241-2.814-.68-1.844.583-7.28-3.494-3.786-.292.098-.68.485-4.852-2.524-2.912.874-1.941-.97-2.039 8.347-1.359 2.33-6.018 14.656-1.747 8.056-1.067.097-5.63 13.395-3.785 5.726-1.36.971-3.494 6.794-.68 7.183-2.134 6.406 1.261 5.824v.097zm35.526-79.689l-.292-.873-.097.68.388.193z"));
        tiles.add(new Tile("UT", TileType.FREE, 0, 3, "M352.385 204.98l-25.042-3.786-13.977-2.232-2.718-.486-.582 3.106-5.145 31.449-10.094 62.8-.486 3.106 2.524.388 25.042 3.98 17.569 2.523 32.71 4.077 2.524.291.194-2.524.776-7.668 1.844-17.665 5.242-50.57.291-2.524-1.941-.194-24.751-3.009-3.786-.485 2.427-18.927.194-1.262-2.815-.388z"));
        tiles.add(new Tile("WY", TileType.FREE, 0, 1, "M461.387 192.167l.776-15.142.971-20.383.097-2.523-1.262-.098-24.945-1.455-13.977-1.165-26.498-2.524-33.196-4.077-1.359 9.513-.097.582-.388 3.203-1.747 12.521-3.3 25.237-.874 6.309-.388 3.203-.194 1.262-2.427 18.927 3.786.485 24.75 3.01 1.942.193 2.33.292 23.974 2.33 19.219 1.552 28.827 1.844 1.942.097.097-2.523.485-10.192.874-17.762.388-7.571.097-2.524.097-2.62z"));
        tiles.add(new Tile("KS", TileType.FREE, 0, 5, "M608.923 312.72l-2.427-41.058-4.368-2.524-.388-1.94-3.494-3.107 3.397-4.174-1.359-2.426-2.523.097-2.33-1.65-.97-.777-3.3.194-35.817 1.36-16.404.388h-32.613l-13.103-.195-3.689-.097v1.845l-.097 5.726-1.068 51.347v1.94l4.174.098 22.519.291 52.705-.776 37.563-1.747 3.689-.292-.097-2.523z"));
        tiles.add(new Tile("NE", TileType.FREE, 0, 2, "M572.33 204.688l-3.009-3.009-10.58-3.106-3.397-.097-5.144 1.456-6.115-3.882-19.122.194-27.857-.291-32.904-1.068-2.912-.097-.097 2.524-.388 7.57-.874 17.763-.485 10.192-.097 2.523 2.329.097 23.683.874 4.66.194-.389 19.024-.097 1.262 3.689.097 13.103.195h32.613l16.404-.389 35.816-1.359 3.3-.194-.388-.388-2.62-4.659-2.233-1.553-2.427-4.66.097-.581v-.292l-3.008-12.23.582-1.94-3.3-4.563-.194-5.435-3.01-4.756-3.105-9.124-.68-.097-1.844-.195z"));
        tiles.add(new Tile("OK", TileType.FREE, 0, 4, "M609.505 322.911l-.194-2.62-.097-2.524-.194-2.524-3.688.292-37.564 1.747-52.705.776-22.519-.291-4.174-.097h-1.553l-6.115-.194-1.94-.097-4.077-.097-1.942-.098-.097 2.524-.097 2.524-.097 2.523-.097 2.621h.582l23.49.582 24.848.292.29 33.195 1.748 5.824 3.883 3.688 4.173.195.874-1.748 5.727 5.242 7.473.776 1.36 1.553 2.717-1.262 6.794 3.204v1.844l2.524-.097 2.62-2.233 4.854 3.106 4.562 1.65 3.106-4.27 9.9 4.659 3.689-3.01 3.009-1.164 2.426.582 9.124-2.62 4.853 2.038 4.368 2.815 3.786.68h.097l-.097-4.369-1.068-26.4-.097-4.369-3.786-19.606-.582-2.718-.097-2.524z"));
        tiles.add(new Tile("SD", TileType.FREE, 0, 3, "M566.895 135.58l-16.404.485-39.505.291-42.805-1.165-3.785-.194-.097 2.233-.388 8.832-.292 6.698-.097 1.359h-.29l-.098 2.523-.97 20.383-.777 15.142-.097 2.621 2.912.097 32.904 1.068 27.857.291 19.122-.194 6.115 3.882 5.144-1.456 3.397.098 10.58 3.106 3.009 3.009 1.844.194v-.68L572.04 201l1.359-5.144-1.65-10.774 2.038-.097-.194-4.465-4.853-34.166-2.524-4.271 3.882-6.406.098-.292-3.3.195z"));
        return tiles;
    }


    }
