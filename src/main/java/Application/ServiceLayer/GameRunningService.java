package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.GameInstance;
import Application.Entities.MobilePlayer;
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
    private Map<String, GameInstance> gameCodeToGameInstance;
    private Map<UUID, GameInstance> mobileIdToGameInstance;
    private Map<UUID, MobilePlayer> mobileIdToMobilePlayer;


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
        mobileIdToGameInstance = new HashMap<>();
        mobileIdToMobilePlayer = new HashMap<>();
        gameCodeToGameInstance = new HashMap<>();
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
        this.userRepository = repositoryFactory.userRepository;
    }

    public Response<GameInstance> startGame(UUID gameId, UUID hostId){
        try{
            Optional<GameInstance> optGameInstance = gameInstanceRepository.findById(gameId);
            if (optGameInstance.isEmpty())
                return Response.fail("there is no game with this UUID");
            GameInstance gameInstance = optGameInstance.get();
            if (!gameInstance.getHost().getId().equals(hostId))
                return Response.fail("wrong host UUID");
            gameInstance.setStatus("RUNNING");
            String gameCode = String.valueOf(Math.round(Math.random()*1000000));
            gameInstance.setGameCode(gameCode);
            gameCodeToGameInstance.put(gameCode, gameInstance);
            gameInstanceRepository.save(gameInstance);
            return Response.ok(gameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<UUID> enterGameWithCode(String gameCode) {
        try {
            GameInstance gameInstance = gameCodeToGameInstance.get(gameCode);
//            if (gameInstance == null)
//                gameInstance = gameInstanceRepository.findBy
            if (gameInstance == null || !gameInstance.getStatus().equals("RUNNING"))
                return Response.fail("game code not valid");
            UUID mobileId = UUID.randomUUID();
            mobileIdToGameInstance.put(mobileId, gameInstance);
            return Response.ok(mobileId);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<GameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            GameInstance gameInstance = mobileIdToGameInstance.get(mobileId);
            if (gameInstance == null || !gameInstance.getStatus().equals("RUNNING"))
                return Response.fail("game code not valid");
            MobilePlayer mobilePlayer = new MobilePlayer(mobileId, name);
            mobileIdToMobilePlayer.put(mobileId, mobilePlayer);
            return Response.ok(gameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
