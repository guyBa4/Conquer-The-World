package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.GameInstance;
import Application.Entities.MobilePlayer;
import Application.Entities.RunningGameInstance;
import Application.Enums.GameStatus;
import Application.Repositories.GameInstanceRepository;
import Application.Repositories.RepositoryFactory;
import Application.Repositories.UserRepository;
import Application.Response;
import org.springframework.stereotype.Service;

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
            if (runningGameInstance == null || !runningGameInstance.getStatus().equals("RUNNING"))
                return Response.fail("game code not valid");
            UUID mobileId = UUID.randomUUID();
            mobileIdToRunningGameInstance.put(mobileId, runningGameInstance);
            return Response.ok(mobileId);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<GameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            RunningGameInstance runningGameInstance = mobileIdToRunningGameInstance.get(mobileId);
            if (runningGameInstance == null || !runningGameInstance.getStatus().equals("RUNNING"))
                return Response.fail("game code not valid");
            MobilePlayer mobilePlayer = new MobilePlayer(mobileId, name);
            runningGameInstance.addMobilePlayer(mobilePlayer);
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<Collection<MobilePlayer>> getWaitingRoomDetails(UUID runningGameUuid) {
        try {
            RunningGameInstance runningGameInstance = runningGamesIdToRunningGameInstance.get(runningGameUuid);
            if (runningGameInstance == null)
                return Response.fail("runningGameUuid not exist");
            return Response.ok(runningGameInstance.getIdToMobilePlayer().values());
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
        Optional<GameInstance> optGameInstance = gameInstanceRepository.findById(runningGameInstance.getId());
        if (optGameInstance.isEmpty())
            throw new IllegalArgumentException("there is no game with this UUID");
        GameInstance gameInstance = optGameInstance.get();
        gameInstance.setStatus(status);
        gameInstanceRepository.save(gameInstance);

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

}
