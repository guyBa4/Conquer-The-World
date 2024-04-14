package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.Entities.*;
import Application.Enums.GameStatus;
import Application.Repositories.*;
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
    private RunningGameInstanceRepository runningGameInstanceRepository;
    private MobilePlayerRepository mobilePlayerRepository;
    private UserRepository userRepository;
    private static Logger LOG;

    public boolean isInit() {
        return isInit;
    }

    private boolean isInit;



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
//        mobileIdToRunningGameInstance = new HashMap<>();
//        gameCodeToRunningGameInstance = new HashMap<>();
//        runningGamesIdToRunningGameInstance = new HashMap<>();
        LOG = getLogger(this.getClass().toString());
//        for (GameInstance gameInstance : gameInstanceRepository.findAll()){
//            gameCodeToRunningGameInstance.put(gameInstance.getGameCode(), gameInstance);
//        }
        isInit = true;
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
        this.userRepository = repositoryFactory.userRepository;
        this.runningGameInstanceRepository = repositoryFactory.runningGameInstanceRepository;
        this.mobilePlayerRepository = repositoryFactory.mobilePlayerRepository;
    }

    public Response<java.util.Map<String, String>> OpenWaitingRoom(UUID gameId, UUID hostId){ // in this function RunningGameInstance is created
        try{

            Optional<GameInstance> optGameInstance = gameInstanceRepository.findById(gameId);
            if (optGameInstance.isEmpty())
                return Response.fail("there is no game with this UUID");
            GameInstance gameInstance = optGameInstance.get();
//            if (!gameInstance.getHost().getId().equals(hostId))
//                return Response.fail("wrong host UUID");
            RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
//            gameCodeToRunningGameInstance.put(runningGameInstance.getCode(), runningGameInstance);
//            runningGamesIdToRunningGameInstance.put(runningGameInstance.getRunningId(), runningGameInstance);
            updateGameStatus(runningGameInstance, GameStatus.WAITING_ROOM.toString());
            runningGameInstanceRepository.save(runningGameInstance);
            java.util.Map<String, String> runningGameInstanceMap = runningGameInstance.toJsonMap();
            return Response.ok(runningGameInstanceMap);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<UUID> enterGameWithCode(String gameCode) {
        try {
            List<RunningGameInstance> runningGameInstanceList = runningGameInstanceRepository.findByCode(gameCode);
            RunningGameInstance runningGameInstance;
            if (runningGameInstanceList.isEmpty())
                return Response.fail("game code not valid");
            runningGameInstance = runningGameInstanceList.get(0);
            MobilePlayer mobilePlayer = new MobilePlayer();
            runningGameInstance.addMobilePlayer(mobilePlayer);
            runningGameInstanceRepository.save(runningGameInstance);
            LOG.info("mobile enter code : " + mobilePlayer.getUuid());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            return Response.ok(mobilePlayer.getUuid());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<RunningGameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            Optional<MobilePlayer> optionalMobilePlayer = mobilePlayerRepository.findById(mobileId);
            if (optionalMobilePlayer.isEmpty())
                return Response.fail("mobile id not valid");
            MobilePlayer mobilePlayer = optionalMobilePlayer.get();
            mobilePlayer.setName(name);
            mobilePlayer.setReady(true);
            RunningGameInstance runningGameInstance = mobilePlayer.getRunningGameInstance();
            runningGameInstanceRepository.save(runningGameInstance);
            LOG.info("mobile enter name : " + mobilePlayer.getName());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<RunningGameInstance> getWaitingRoomDetails(UUID runningGameid) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameid);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
            return Response.ok(runningGameInstance);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    public Response<java.util.Map<String, String>> startGame(UUID runningGameid) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameid);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
            updateGameStatus(runningGameInstance, GameStatus.STARTED.toString());
            runningGameInstance.assignGroups();
            runningGameInstanceRepository.save(runningGameInstance);
            java.util.Map<String, String> runningGameInstanceMap = runningGameInstance.toJsonMap();
            return Response.ok(runningGameInstanceMap);
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


    public Response<RunningGameInstance> getRunningGame(UUID runningGameId, UUID userId) {
        try {
            List<RunningGameInstance> runningGameInstanceList = runningGameInstanceRepository.findByRunningIdAndMobilePlayers_id(runningGameId, userId);
            RunningGameInstance runningGameInstance;
            if (runningGameInstanceList.isEmpty()) {
                Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameId);
                if (optionalRunningGameInstance.isEmpty()){
                    LOG.warning("Game ID does not exist");
                    return Response.fail("Game ID does not exist");
                }else{
                    runningGameInstance = optionalRunningGameInstance.get();
                    LOG.warning("User ID not of host or registered player");
                    LOG.warning("userId = " + userId);
                    for (MobilePlayer mobilePlayer : runningGameInstance.getMobilePlayers()){
                        LOG.warning(mobilePlayer.toString());
                    }
                }
                return Response.fail("Unauthorized request");
            }else{
                runningGameInstance = runningGameInstanceList.get(0);
                return Response.ok(runningGameInstance);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    public Response<List<RunningTile>> getRunningTiles(UUID runningGameId) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameId);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
                return Response.ok(runningGameInstance.getTiles());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }


    public Response<AssignedQuestion> getQuestion(int difficulty, UUID runningGameId) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameId);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
            AssignedQuestion question = runningGameInstance.getQuestion(difficulty);
            return Response.ok(question);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    public Response<Boolean> checkAnswer(UUID runningGameId, String tileId, UUID userId, UUID questionId, String answer) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameId);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
            MobilePlayer player = runningGameInstance.getPlayer(userId);
            if (player == null) {
                LOG.severe("Did not find user with ID: " + userId.toString());
                return Response.fail("Did not find user by ID");
            }
            boolean isCorrect = runningGameInstance.checkAnswer(tileId, player.getGroup(), questionId, answer, repositoryFactory.answerRepository);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(isCorrect);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            LOG.severe("Exception caught: " + e.getMessage());
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    public Response<Boolean> endRunningGame(UUID runningGameId) {
        try {
            Optional<RunningGameInstance> optionalRunningGameInstance = runningGameInstanceRepository.findById(runningGameId);
            RunningGameInstance runningGameInstance;
            if (optionalRunningGameInstance.isEmpty())
                return Response.fail("game id not valid");
            runningGameInstance = optionalRunningGameInstance.get();
            runningGameInstanceRepository.delete(runningGameInstance);
            return Response.ok(true);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
