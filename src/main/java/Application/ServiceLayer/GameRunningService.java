package Application.ServiceLayer;

import Application.APILayer.JsonToInstance;
import Application.Configurations.Configuration;
import Application.DataAccessLayer.DALController;
import Application.Entities.*;
import Application.Enums.GameStatus;
import Application.Events.Event;
import Application.Events.EventRecipient;
import Application.Events.EventType;
import Application.Repositories.*;
import Application.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.logging.*;
import static java.util.logging.Logger.getLogger;
import java.util.*;

@Service
public class GameRunningService {
    private RepositoryFactory repositoryFactory;
    private RunningGameInstanceRepository runningGameInstanceRepository;
    private DALController dalController;
    private EventService eventService;
    private static Logger LOG = getLogger(GameRunningService.class.toString());


    @Autowired
    private GameRunningService(RepositoryFactory repositoryFactory, EventService eventService){
        this.dalController = DALController.getInstance();
        this.repositoryFactory = repositoryFactory;
        this.runningGameInstanceRepository = repositoryFactory.runningGameInstanceRepository;
        this.eventService = eventService;
    }
    
    public void publishEvent(EventType eventType, Object eventBody, RunningGameInstance gameToUpdate) {
        List<EventRecipient> eventRecipients = new ArrayList<>(gameToUpdate.getMobilePlayers());
        eventRecipients.add(gameToUpdate.getGameInstance().getHost());
        Event event = new Event()
                .setEventType(eventType)
                .setBody(eventBody)
                .setRecipients(eventRecipients)
                .setTimestamp(Date.from(Instant.now()));
        eventService.addEvent(event);
    }

    public Response<RunningGameInstance> OpenWaitingRoom(UUID gameId, UUID hostId){ // in this function RunningGameInstance is created
        try{
            GameInstance gameInstance = dalController.getGameInstance(gameId);
            RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
            updateGameStatus(runningGameInstance, GameStatus.WAITING_ROOM);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
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
            LOG.info("mobile enter code : " + mobilePlayer.getId());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            mobilePlayer.setEventEmitter(new SseEmitter(Configuration.defaultSseEmitterTimeout));
            return Response.ok(mobilePlayer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<RunningGameInstance> addMobileDetails(UUID mobileId, String name) {
        try {
            if (name == null || name.length() < 2) {
                LOG.warning("Mobile player name must be longer than 2 characters.");
                throw new IllegalArgumentException("Mobile player name must be longer than 2 characters.");
            }
            MobilePlayer mobilePlayer = dalController.getMobilePlayer(mobileId);
            mobilePlayer.setName(name);
            mobilePlayer.setReady(true);
            RunningGameInstance runningGameInstance = mobilePlayer.getRunningGameInstance();
            runningGameInstanceRepository.save(runningGameInstance);
            LOG.info("New mobile player name: " + mobilePlayer.getName() + "for running game instance with id: " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
            publishEvent(EventType.WAITING_ROOM_UPDATE, mobilePlayer, runningGameInstance);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<RunningGameInstance> getWaitingRoomDetails(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<RunningGameInstance> startGame(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            if (runningGameInstance != null) {
                updateGameStatus(runningGameInstance, GameStatus.STARTED);
                runningGameInstance.assignGroups();
                runningGameInstance.initStartingPositions();
                initQuestionQueues(runningGameInstance);
                runningGameInstanceRepository.save(runningGameInstance);
                LOG.info("Game with ID " + runningGameId.toString() + " started successfully");
                return Response.ok(runningGameInstance);
            }
            else {
                LOG.warning("Game with ID " + runningGameId.toString() + " not found.");
                return Response.fail(400, "Game not found");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    private void initQuestionQueues(RunningGameInstance runningGameInstance) {
        Questionnaire questionnaire = runningGameInstance.getQuestionnaire();
        UUID id = questionnaire.getId();
        List<Group> groups = runningGameInstance.getGroups();
        List<Group> groupsToAssign = groups.stream().filter((group) -> group.getNumber() != 0).toList();
        for(int difficulity = 1; difficulity <= 5; difficulity++){
            List<AssignedQuestion> questionList = this.repositoryFactory.assignedQuestionRepository.findByQuestionnaireIdAndDifficultyLevel(id, difficulity);
            for (Group group : groupsToAssign){
                group.addQuestionQueue(difficulity, questionList);
            }
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
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
    public Response<List<RunningTile>> getRunningTiles(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
                return Response.ok(runningGameInstance.getTiles());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }


    public Response<AssignedQuestion> getQuestion(UUID runningTileId, int group, UUID runningGameId, UUID authorizationToken) {
        try {
            if (runningTileId == null) {
                LOG.warning("Received null running tile ID");
                return Response.fail("Received null running tile ID");
            }
            else if (group < 1) {
                LOG.warning("Received invalid group number");
                return Response.fail("Received invalid group number");
            }
            else if (runningGameId == null) {
                LOG.warning("Received null running game ID");
                return Response.fail("Received null running game ID");
            }
            else if (authorizationToken == null) {
                LOG.warning("Received null auth token");
                return Response.fail("Received null auth token");
    
            }
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            MobilePlayer player = getMobilePlayerByAuthToken(runningGameInstance, authorizationToken);
            Group playerGroup = player.getGroup();
            if (playerGroup == null) {
                LOG.warning("Failed to validate player group");
                return Response.fail("Failed to validate player group");
            }
            else if (group != playerGroup.getNumber() ||
                    playerGroup.getId() == null ||
                    runningGameInstance.getGroupByNumber(group) == null ||
                    !playerGroup.getId().equals(runningGameInstance.getGroupByNumber(group).getId())){
                LOG.warning("Requesting player group does not match passed group number.");
                return Response.fail("Requesting player group does not match passed group number.");
            }
            AssignedQuestion question = runningGameInstance.getQuestion(runningTileId, group, player);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(question);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
    
    private MobilePlayer getMobilePlayerByAuthToken(RunningGameInstance game, UUID authorizationToken) {    // TODO: Change implementation to use actual tokens
        if (game != null && authorizationToken != null)
            return game.getPlayer(authorizationToken);
        return null;
    }
    
    public Response<Boolean> checkAnswer(UUID runningGameId, UUID tileId, UUID userId, UUID questionId, String answer) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            MobilePlayer player = runningGameInstance.getPlayer(userId);
            if (player == null) {
                LOG.severe("Did not find user with ID: " + userId.toString());
                return Response.fail("Did not find user by ID");
            }
            boolean isCorrect = runningGameInstance.checkAnswer(tileId, player, questionId, answer, repositoryFactory.answerRepository);
            runningGameInstanceRepository.save(runningGameInstance);
            return Response.ok(isCorrect);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<Boolean> endRunningGame(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            runningGameInstanceRepository.delete(runningGameInstance);
            return Response.ok(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
}
