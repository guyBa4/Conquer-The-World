package Application.ServiceLayer;

import Application.APILayer.Responses.RunningTileResponse;
import Application.APILayer.Responses.ValidateAnswerResponse;
import Application.DataAccessLayer.DALController;
import Application.DataAccessLayer.Repositories.MobilePlayerRepository;
import Application.DataAccessLayer.Repositories.MobileUserRepository;
import Application.Entities.games.GameInstance;
import Application.Entities.games.GameStatistic;
import Application.Entities.games.RunningGameInstance;
import Application.Entities.games.RunningTile;
import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.Questionnaire;
import Application.Entities.users.Group;
import Application.Entities.users.MobilePlayer;
import Application.Entities.users.MobileUser;
import Application.Entities.users.PlayerStatistics;
import Application.Enums.GameStatus;
import Application.Events.Event;
import Application.Events.EventType;
import Application.DataAccessLayer.Repositories.RepositoryFactory;
import Application.DataAccessLayer.Repositories.RunningGameInstanceRepository;
import Application.Response;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@Service
public class GameRunningService {
    private RepositoryFactory repositoryFactory;
    private RunningGameInstanceRepository runningGameInstanceRepository;
    private MobilePlayerRepository mobilePlayerRepository;
    private MobileUserRepository mobileUserRepository;
    private DALController dalController;
    private EventService eventService;
    private Map<Pair<UUID, UUID>, TimerTask> timers;
    private static Logger LOG = getLogger(GameRunningService.class.toString());

    public GameRunningService(){}

    @Autowired
    private GameRunningService(RepositoryFactory repositoryFactory, EventService eventService, MobilePlayerRepository mobilePlayerRepository,
                               MobileUserRepository mobileUserRepository){
        this.dalController = DALController.getInstance();
        if (dalController.needToInitiate())
            dalController.init(repositoryFactory);
        this.repositoryFactory = repositoryFactory;
        this.mobilePlayerRepository = mobilePlayerRepository;
        this.mobileUserRepository = mobileUserRepository;
        this.runningGameInstanceRepository = repositoryFactory.runningGameInstanceRepository;
        this.eventService = eventService;
        this.timers = new HashMap<>();
    }

    public GameRunningService setDalController(DALController dalController) {
        this.dalController = dalController;
        return this;
    }
    public void publishEvent(EventType eventType, Object eventBody, RunningGameInstance gameToUpdate) {
        Event event = new Event()
                .setEventType(eventType)
                .setBody(eventBody)
                .setTimestamp(Date.from(Instant.now()));
        eventService.addEvent(gameToUpdate.getRunningId(), event);
    }

    public Response<RunningGameInstance> OpenWaitingRoom(UUID gameId, UUID hostId){ // in this function RunningGameInstance is created
        try{
            GameInstance gameInstance = dalController.getGameInstance(gameId);
            RunningGameInstance runningGameInstance = new RunningGameInstance(gameInstance);
            runningGameInstance.setStatus(GameStatus.WAITING_ROOM);
            runningGameInstanceRepository.save(runningGameInstance);
            eventService.addNewEventList(gameId);
            return Response.ok(runningGameInstance);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<MobilePlayer> enterGameWithCode(String gameCode, String mobileUserId) {
        try {
            List<RunningGameInstance> runningGameInstanceList = runningGameInstanceRepository.findByCode(gameCode);
            RunningGameInstance runningGameInstance;
            if (runningGameInstanceList.isEmpty())
                return Response.fail("game code not valid");
            runningGameInstance = runningGameInstanceList.get(0);
            MobilePlayer mobilePlayer = new MobilePlayer();
            if (mobileUserId != null) {
                Optional<MobileUser> mobileUserOpt = mobileUserRepository.findById(UUID.fromString(mobileUserId));
                MobileUser mobileUser = mobileUserOpt.orElse(null);
                if (mobileUser == null) {
                    throw new IllegalArgumentException("Invalid mobile user id");
                }
                mobilePlayer.setMobileUser(mobileUser);
            }
            runningGameInstance.addMobilePlayer(mobilePlayer);
            runningGameInstanceRepository.save(runningGameInstance);
            LOG.info("mobile enter code : " + mobilePlayer.getId());
            LOG.info("for running game instance with id : " + runningGameInstance.getRunningId() + " : " + runningGameInstance.getName());
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
                runningGameInstance.assignGroups();
                runningGameInstance.initStartingPositions();
                initQuestionQueues(runningGameInstance);
                runningGameInstance.setStatus(GameStatus.STARTED);
                GameStatistic gameStatistic = new GameStatistic(runningGameInstance);
                runningGameInstance.setGameStatistics(gameStatistic);
                runningGameInstanceRepository.save(runningGameInstance);
                eventService.addNewEventList(runningGameId);
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
                } else {
                    runningGameInstance = optionalRunningGameInstance.get();
                    return Response.ok(runningGameInstance);
                }
            } else {
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
            if (runningGameInstance == null || !runningGameInstance.getStatus().name().equals(GameStatus.STARTED.name()))
                throw new IllegalArgumentException("Can not get question from a non-started game.");
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
            if (runningGameInstance == null || !runningGameInstance.getStatus().name().equals(GameStatus.STARTED.name()))
                throw new IllegalArgumentException("Can not get question from a non-started game.");
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
            if (question != null) {
                RunningTile tileToUpdate = runningGameInstance.getTileById(runningTileId);
                setQuestionTimeout(question, runningGameInstance, tileToUpdate);
                publishEvent(EventType.TILES_UPDATE, RunningTileResponse.from(tileToUpdate), runningGameInstance);
            }
            runningGameInstanceRepository.save(runningGameInstance);
            
            return Response.ok(question);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
//            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
    
    private void setQuestionTimeout(AssignedQuestion question, RunningGameInstance runningGameInstance, RunningTile tile) {
        GameInstance gameInstance = runningGameInstance.getGameInstance();
        long questionTimeout = gameInstance.getConfiguration().getQuestionTimeLimit() * 1000L; // Question timeout in milliseconds
        if (questionTimeout > 0) {
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                @Transactional
                public void run() {
                    RunningTile fetchedTile = dalController.getRunningTile(tile.getId());
                    if (fetchedTile.getActiveQuestion() != null && fetchedTile.getActiveQuestion().getId().equals(question.getId())
                        && fetchedTile.getAnsweringPlayer() != null) {
                        LOG.info("Timeout reached for question " + question.getId());
                        AssignedQuestion question = fetchedTile.getActiveQuestion();
                        MobilePlayer answeringPlayer = fetchedTile.getAnsweringPlayer();
                        fetchedTile.setActiveQuestion(null)
                                .setAnsweringGroup(null)
                                .setAnsweringPlayer(null)
                                .setNumberOfCorrectAnswers(0);
                        repositoryFactory.runningTileRepository.save(fetchedTile);
                        timers.remove(Pair.of(question.getId(), answeringPlayer.getId()));
                    }
                }
            };
            timers.put(Pair.of(question.getId(), tile.getAnsweringPlayer().getId()), timerTask);
            question.setTimeout(questionTimeout);
            timer.schedule(timerTask, questionTimeout + (3 * 1000L)); // Added buffer time
        }
    }
    
    private MobilePlayer getMobilePlayerByAuthToken(RunningGameInstance game, UUID authorizationToken) {    // TODO: Change implementation to use actual tokens
        if (game != null && authorizationToken != null)
            return game.getPlayer(authorizationToken);
        return null;
    }
    
    @Transactional
    public Response<ValidateAnswerResponse> checkAnswer(UUID runningGameId, UUID tileId, UUID userId, UUID questionId, String answer) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            if (!runningGameInstance.getStatus().name().equals(GameStatus.STARTED.name()))
                throw new IllegalArgumentException("Can not answer a question from a non-started game.");
            MobilePlayer player = runningGameInstance.getPlayer(userId);
            if (player == null) {
                LOG.severe("Did not find user with ID: " + userId.toString());
                return Response.fail("Did not find user by ID");
            }
    
            PlayerStatistics playerStatistics = repositoryFactory.playerStatisticRepository.findByRunningGameInstanceRunningIdAndMobilePlayerId(runningGameId, userId).get(0);
            playerStatistics.addQuestionsAnswered();
            runningGameInstance.getGameStatistics().addQuestionsAnswered();
    
            ValidateAnswerResponse res = new ValidateAnswerResponse();
            RunningTile tile = runningGameInstance.getTileById(tileId);
            res.setCorrect(runningGameInstance.checkAnswer(tile, player, questionId, answer, repositoryFactory.answerRepository));
    
            if (res.isCorrect()) {
                Group playerGroup = player.getGroup();

                playerStatistics.addCorrectAnswers();
                runningGameInstance.getGameStatistics().addCorrectAnswers();
                
                if (runningGameInstance.getGameInstance().getConfiguration().getMultipleQuestionsPerTile()) {
                    tile.incrementNumberOfCorrectAnswers();
                    if (!tile.isAllQuestionsAnswered()) {
                        Response<AssignedQuestion> questionResponse = getQuestion(tileId, player.getGroup().getNumber(),
                                runningGameId, userId);
                        if (questionResponse.isSuccessful()) {
                            AssignedQuestion question = questionResponse.getValue();
                            tile.setActiveQuestion(question);
                            res.setNextQuestion(question);
                            publishEvent(EventType.TILES_UPDATE, RunningTileResponse.from(tile), runningGameInstance);
                        }
                    } else setTileConquered(tile, playerGroup, runningGameInstance, playerStatistics);
                } else setTileConquered(tile, playerGroup, runningGameInstance, playerStatistics);
            } else {
                tile.setAnsweringPlayer(null)
                        .setAnsweringGroup(null)
                        .setActiveQuestion(null)
                        .setNumberOfCorrectAnswers(0);
                publishEvent(EventType.TILES_UPDATE, RunningTileResponse.from(tile), runningGameInstance);
            }
            runningGameInstanceRepository.save(runningGameInstance);
            TimerTask task = timers.getOrDefault(Pair.of(questionId, userId), null);
            if (task != null)
                task.cancel();
            return Response.ok(res);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, "Internal Server Error");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error");
        }
    }

    private void setTileConquered(RunningTile tile, Group group, RunningGameInstance runningGameInstance, PlayerStatistics playerStatistics) {
        tile.setAnsweringPlayer(null)
                .setAnsweringGroup(null)
                .setControllingGroup(group)
                .setActiveQuestion(null)
                .setNumberOfCorrectAnswers(0);
        group.addScore(tile.getTile().getDifficultyLevel());
        publishEvent(EventType.TILES_UPDATE, RunningTileResponse.from(tile), runningGameInstance);
        publishEvent(EventType.SCORE_UPDATE, group, runningGameInstance);
        playerStatistics.addScore(tile.getTile().getDifficultyLevel());
    }
    
    public Response<Boolean> endRunningGame(UUID runningGameId) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(runningGameId);
            if (!runningGameInstance.getStatus().name().equals(GameStatus.STARTED.name())) {
                throw new IllegalArgumentException("Cannot end a non-started game");
            }
            runningGameInstance.setStatus(GameStatus.ENDED);
            runningGameInstance.getGameStatistics().setTimeEnded(new Date());
            runningGameInstanceRepository.save(runningGameInstance);
            publishEvent(EventType.END_GAME_UPDATE, null, runningGameInstance);
            return Response.ok(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }


    public Response<Map<String, Object>> getRunningGameInstanceLean(UUID id){
        RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(id);
        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("id", runningGameInstance.getRunningId());
        gameMap.put("name", runningGameInstance.getName());
        gameMap.put("mobilePlayers", runningGameInstance.getMobilePlayers());
        gameMap.put("groups", runningGameInstance.getGroups());
        gameMap.put("code", runningGameInstance.getCode());
        gameMap.put("status", runningGameInstance.getStatus());
        return Response.ok(gameMap);
    }

    public Response<GameStatistic> getGameStatistic(UUID id) {
        try {
            RunningGameInstance runningGameInstance = dalController.getRunningGameInstance(id);
            GameStatistic gameStatistic = runningGameInstance.getGameStatistics();
            return Response.ok(gameStatistic);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<List<GameStatistic>> getAllGameStatistics() {
        try {
            List<GameStatistic> gameStatistics = repositoryFactory.gameStatisticRepository.findAll();
            return Response.ok(gameStatistics);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<List<PlayerStatistics>> getAllPlayerStatistics(UUID running_game_id) {
        try {
            List<PlayerStatistics> playerStatistics = repositoryFactory.playerStatisticRepository.findByRunningGameInstanceRunningId(running_game_id);
            return Response.ok(playerStatistics);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }

    public Response<PlayerStatistics> getPlayerStatistic(UUID playerId, UUID runningId) {
        try {
            PlayerStatistics playerStatistics = repositoryFactory.playerStatisticRepository.findByRunningGameInstanceRunningIdAndMobilePlayerId(runningId, playerId).get(0);
            return Response.ok(playerStatistics);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
    
    public Response<Boolean> reportCheater(UUID playerId, UUID runningGameId) {
        try {
            if (runningGameId != null && playerId != null) {
                Response<RunningGameInstance> runningGameResponse = getRunningGame(runningGameId, playerId);
                if (runningGameResponse.isSuccessful() && runningGameResponse.getValue().getStatus().equals(GameStatus.STARTED)) {
                    RunningGameInstance runningGameInstance = runningGameResponse.getValue();
                    MobilePlayer player = runningGameInstance.getPlayer(playerId);
                    if (player != null) {
                        player.getPlayerStatistics().setCheated(true);
                        mobilePlayerRepository.save(player);
                        publishEvent(EventType.CHEATING_PLAYER_UPDATE, player, runningGameInstance);
                        return Response.ok(true);
                    } else {
                        return Response.fail(400, "Mobile player not found");
                    }
                } else {
                    return Response.fail(400, "Invalid running game");
                }
            } else {
                return Response.fail(400, "Invalid request");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(500, "Internal Server Error : \n" + e.getMessage());
        }
    }
}
