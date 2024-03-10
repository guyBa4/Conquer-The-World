package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.GameInstance;
import Application.Entities.MobilePlayer;
import Application.Entities.Question;
import Application.Entities.RunningGameInstance;
import Application.Enums.GameStatus;
import Application.Repositories.GameInstanceRepository;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import Application.Response;
import org.springframework.stereotype.Service;
import java.util.logging.*;
import static java.util.logging.Logger.getLogger;
import java.util.*;

@Service
public class GameRunningService {
    private static GameRunningService instance = null;
    private static JsonToInstance jsonToInstance;
    private static final Object instanceLock = new Object();
    private RepositoryFactory repositoryFactory;
    private GameInstanceRepository gameInstanceRepository;
    private UserRepository userRepository;
    private Map<String, RunningGameInstance> gameCodeToRunningGameInstance;
    private Map<UUID, RunningGameInstance> mobileIdToRunningGameInstance;
    private Map<UUID, RunningGameInstance> runningGamesIdToRunningGameInstance;
    private static Logger LOG;



    private GameRunningService(){}

    public static GameRunningService getInstance() {
        synchronized (instanceLock) {
            if (instance == null)
                instance = new GameRunningService();
        }
        return instance;
    }

    public void init(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
        jsonToInstance = JsonToInstance.getInstance();
        setRepositories(repositoryFactory);
        mobileIdToRunningGameInstance = new HashMap<>();
        gameCodeToRunningGameInstance = new HashMap<>();
        runningGamesIdToRunningGameInstance = new HashMap<>();
        LOG = getLogger(this.getClass().toString());
//        for (GameInstance gameInstance : gameInstanceRepository.findAll()){
//            gameCodeToRunningGameInstance.put(gameInstance.getGameCode(), gameInstance);
//        }
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
        this.userRepository = repositoryFactory.userRepository;
    }

    public Response<RunningGameInstance> OpenWaitingRoom(UUID gameId, UUID hostId){
        try{
            Optional<GameInstance> optGameInstance = gameInstanceRepository.findById(gameId);
            if (optGameInstance.isEmpty())
                return Response.fail("there is no game with this UUID");
            GameInstance gameInstance = optGameInstance.get();
            if (!gameInstance.getHost().getId().equals(hostId))
                return Response.fail("wrong host UUID");
            RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
            gameCodeToRunningGameInstance.put(runningGameInstance.getCode(), runningGameInstance);
            runningGamesIdToRunningGameInstance.put(runningGameInstance.getRunningId(), runningGameInstance);
            updateGameStatus(runningGameInstance, GameStatus.WAITING_ROOM.toString());
            gameInstanceRepository.save(gameInstance);
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<UUID> enterGameWithCode(String gameCode) {
        try {
            RunningGameInstance runningGameInstance = gameCodeToRunningGameInstance.get(gameCode);
            if (runningGameInstance == null || !(runningGameInstance.getStatus().equals(GameStatus.WAITING_ROOM.toString()) || runningGameInstance.getStatus().equals(GameStatus.STARTED.toString())))
                return Response.fail("game code not valid");
            UUID mobileId = UUID.randomUUID();
            mobileIdToRunningGameInstance.put(mobileId, runningGameInstance);
            LOG.info("mobile enter code : " + mobileId);
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            return Response.ok(mobileId);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<GameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            RunningGameInstance runningGameInstance = mobileIdToRunningGameInstance.get(mobileId);
            if (runningGameInstance == null || !(runningGameInstance.getStatus().equals(GameStatus.WAITING_ROOM.toString()) || runningGameInstance.getStatus().equals(GameStatus.STARTED.toString())))
                return Response.fail("game code not valid");
            MobilePlayer mobilePlayer = new MobilePlayer(mobileId, name);
            runningGameInstance.addMobilePlayer(mobilePlayer);
            LOG.info("mobile enter name : " + mobilePlayer.getName());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<RunningGameInstance> getWaitingRoomDetails(UUID runningGameUuid) {
        try {
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    public Response<RunningGameInstance> startGame(UUID runningGameUuid) {
        try {
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            updateGameStatus(runningGameInstance, GameStatus.STARTED.toString());
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    private void updateGameStatus(RunningGameInstance runningGameInstance, String status){
        runningGameInstance.setStatus(status);
//        Optional<GameInstance> optGameInstance = gameInstanceRepository.findById(runningGameInstance.getId());
//        if (optGameInstance.isEmpty())
//            throw new IllegalArgumentException("there is no game with this UUID");
//        GameInstance gameInstance = optGameInstance.get();
//        gameInstance.setStatus(status);
//        gameInstanceRepository.save(gameInstance);

    }


    public Response<RunningGameInstance> getRunningGame(UUID runningGameUuid) {
        try {
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public void addRunningGame(RunningGameInstance runningGameInstance){
        this.runningGamesIdToRunningGameInstance.put(runningGameInstance.getRunningId(), runningGameInstance);
        this.gameCodeToRunningGameInstance.put(runningGameInstance.getCode(), runningGameInstance);
        runningGameInstance.setStatus(GameStatus.WAITING_ROOM.toString());
    }


    public Map<UUID, RunningGameInstance> getRunningGamesIdToRunningGameInstance() {
        return runningGamesIdToRunningGameInstance;
    }

    public void setRunningGamesIdToRunningGameInstance(Map<UUID, RunningGameInstance> runningGamesIdToRunningGameInstance) {
        this.runningGamesIdToRunningGameInstance = runningGamesIdToRunningGameInstance;
    }

    public Response<Question> getQuestion(int difficulty, String runningGameid) {
        try {
            UUID runningGameUuid = UUID.fromString(runningGameid);
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            Question question = runningGameInstance.getQuestion(difficulty);
            return Response.ok(question);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    public Response<Boolean> checkAnswer( UUID runningGameid, UUID questionId, String answer) {
        try {
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            return Response.ok( runningGameInstance.checkAnswer(questionId, answer));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

//    public Response<Boolean> validateAnswer(UUID runningGameUuid, UUID questionUuid, String answer) {
//        try {
//            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
//            if (runningGameInstance == null)
//                return Response.fail("runningGameUuid not exist");
//            return Response.ok( runningGameInstance.checkAnswer(questionId, answer));
//        } catch (Exception e) {
//            e.printStackTrace(); // Log the exception or handle it appropriately
//            return Response.fail(500, "Internal Server Error"); // Internal Server Error
//        }
//    }
}
