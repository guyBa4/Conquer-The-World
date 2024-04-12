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

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
        this.gameInstanceMap = new HashMap<>();
        this.gameRunningService = GameRunningService.getInstance();
        if (gameRunningService.getRunningGamesIdToRunningGameInstance() == null)
            gameRunningService.init(repositoryFactory);

        // init objects:
//        List<Question> questionList = new LinkedList<>();
//        Question question1 = new Question(UUID.randomUUID(), false, "1 + 5 = ?", "6",new String[2], 3, new String[2]);
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
