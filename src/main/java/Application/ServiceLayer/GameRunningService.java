package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.DataAccessLayer.DALController;
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
    private DALController dalController;
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
        this.dalController = DALController.getInstance();
        LOG = getLogger(this.getClass().toString());

        isInit = true;
    }

    private void setRepositories(RepositoryFactory repositoryFactory) {
        this.gameInstanceRepository = repositoryFactory.gameInstanceRepository;
        this.userRepository = repositoryFactory.userRepository;
        this.runningGameInstanceRepository = repositoryFactory.runningGameInstanceRepository;
        this.mobilePlayerRepository = repositoryFactory.mobilePlayerRepository;
    }

    public Response<RunningGameInstance> OpenWaitingRoom(UUID gameId, UUID hostId){ // in this function RunningGameInstance is created
        try{
            GameInstance gameInstance = dalController.getGameInstance(gameId);
//            if (!gameInstance.getHost().getId().equals(hostId))
//                return Response.fail("wrong host UUID");
            RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
            updateGameStatus(runningGameInstance, GameStatus.WAITING_ROOM);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    public Response<MobilePlayer> enterGameWithCode(String gameCode) {
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
            return Response.ok(mobilePlayer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    public Response<RunningGameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            MobilePlayer mobilePlayer = dalController.getMobilePlayer(mobileId);
            mobilePlayer.setName(name);
            mobilePlayer.setReady(true);
            RunningGameInstance runningGameInstance = mobilePlayer.getRunningGameInstance();
            runningGameInstanceRepository.save(runningGameInstance);
            LOG.info("mobile enter name : " + mobilePlayer.getName());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    public Response<RunningGameInstance> getWaitingRoomDetails(UUID runningGameid) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameid);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }
    public Response<RunningGameInstance> startGame(UUID runningGameid) {
        try {
            RunningGameInstance runningGameInstance =dalController.getRunningGameInstance(runningGameid);
            updateGameStatus(runningGameInstance, GameStatus.STARTED);
            runningGameInstance.assignGroups();
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    private void updateGameStatus(RunningGameInstance runningGameInstance, GameStatus status){
        runningGameInstance.setStatus(status);
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

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }
    public Response<List<RunningTile>> getRunningTiles(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
                return Response.ok(runningGameInstance.getTiles());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }


    public Response<AssignedQuestion> getQuestion(int difficulty, UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            AssignedQuestion question = runningGameInstance.getQuestion(difficulty);
            return Response.ok(question);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }
    public Response<Boolean> checkAnswer(UUID runningGameId, String tileId, UUID userId, UUID questionId, String answer) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            MobilePlayer player = runningGameInstance.getPlayer(userId);
            if (player == null) {
                LOG.severe("Did not find user with ID: " + userId.toString());
                return Response.fail("Did not find user by ID");
            }
            boolean isCorrect = runningGameInstance.checkAnswer(tileId, player.getGroup(), questionId, answer, repositoryFactory.answerRepository);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(isCorrect);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.toString());
        }
    }

    public Response<Boolean> endRunningGame(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            runningGameInstanceRepository.delete(runningGameInstance);
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
