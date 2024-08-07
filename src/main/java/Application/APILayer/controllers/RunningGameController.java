package Application.APILayer.controllers;

import Application.APILayer.Responses.MapResponse;
import Application.APILayer.Responses.ValidateAnswerResponse;
import Application.APILayer.TokenHandler;
import Application.Entities.games.GameStatistic;
import Application.Entities.games.RunningGameInstance;
import Application.Entities.games.RunningTile;
import Application.Entities.questions.AssignedQuestion;
import Application.Entities.users.MobilePlayer;
import Application.Entities.users.PlayerStatistics;
import Application.DataAccessLayer.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.EventService;
import Application.ServiceLayer.GameRunningService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@RestController
@RequestMapping(path = "running_game")
@CrossOrigin(origins = "*")
public class RunningGameController {
    private final ReentrantLock lock = new ReentrantLock();
    TokenHandler tokenHandler;
    GameRunningService gameRunningService;
    EventService eventService;
    private static Logger LOG;
    
    @Autowired
    public RunningGameController(RepositoryFactory repositoryFactory, GameRunningService gameRunningService, EventService eventService)
    {
        this.gameRunningService = gameRunningService;
        this.eventService = eventService;
        tokenHandler = TokenHandler.getInstance();
        LOG = getLogger(this.getClass().toString());
    }

    @PostMapping(path = "/open_waiting_room")
    @ResponseBody
    public Response<RunningGameInstance> openWaitingRoom(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /open_waiting_room endpoint:\n" + jsonObj);
            UUID gameId = UUID.fromString(jsonObj.getString("gameId"));
            UUID userId = UUID.fromString(jsonObj.getString("userId"));
            return gameRunningService.OpenWaitingRoom(gameId, userId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/enter_game_code")
    @ResponseBody
    public Response<MobilePlayer> enterGame(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /enter_game_code endpoint:\n" + jsonObj);
            String gameCode = jsonObj.getString("gameCode");
            String mobileUserId = jsonObj.has("mobileUserId") && !jsonObj.isNull("mobileUserId") ? jsonObj.getString("mobileUserId") : null;
            return gameRunningService.enterGameWithCode(gameCode, mobileUserId);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/enter_player_details")
    @ResponseBody
    public Response<RunningGameInstance> addMobileDetails(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            String name = jsonObj.getString("name");
            String mobileId = jsonObj.getString("mobileId");
//            int group = jsonObj.getInt("group");
            UUID mobileUUid = UUID.fromString(mobileId);
            return gameRunningService.addMobileDetails(mobileUUid, name);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }

    }

    @GetMapping(path = "/get_waiting_room/{game_id}")
    public Response<RunningGameInstance> getWaitingRoomDetails(@PathVariable (name= "game_id") String gameId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            UUID gameUuid = UUID.fromString(gameId);
            return gameRunningService.getWaitingRoomDetails(gameUuid);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/start_game")
    @ResponseBody
    public Response<RunningGameInstance> startGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
//            LOG.info("Request received by /start_game endpoint:\n " + jsonObj);
            UUID gameId = UUID.fromString(jsonObj.getString("gameId"));
            return gameRunningService.startGame(gameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/get_running_game/{gameId}")
    public Response<RunningGameInstance> getRunningGame(@PathVariable (name= "gameId") String gameId,
                                                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            UUID runningGameUuid = UUID.fromString(gameId);
            UUID mobileId = UUID.fromString(authorizationHeader);
            LOG.info("Request received by /get_running_game endpoint:\n " + gameId);
            return gameRunningService.getRunningGame(runningGameUuid, mobileId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/generate_question/game_id/{runningGameId}/group/{group}/tile/{runningTileId}")
    public Response<AssignedQuestion> getQuestion(@PathVariable(name= "group") int group, @PathVariable(name= "runningGameId") String runningGameId,
                                                  @PathVariable(name= "runningTileId") String runningTileId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            lock.lock();
            tokenHandler.verifyAnyToken(authorizationHeader);
            LOG.info(String.format("Request received by /generate_question endpoint:\n {'group': %s, 'runningGameId': %s, 'runningTileId': %s}", group, runningGameId, runningTileId));
            return gameRunningService.getQuestion(UUID.fromString(runningTileId), group, UUID.fromString(runningGameId), UUID.fromString(authorizationHeader));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }finally {
            lock.unlock();
        }
    }

    @PostMapping(path = "/validate_answer")
    public Response<ValidateAnswerResponse> validateAnswer(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader){
//                                            ,@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /validate_answer endpoint:\n" + jsonObj);
            UUID runningGameId = UUID.fromString(jsonObj.getString("gameId"));
            UUID questionUuid = UUID.fromString(jsonObj.getString("questionId"));
            String tileId = jsonObj.getString("tileId");
            UUID tileUUID = UUID.fromString(tileId);
            String mobileId = jsonObj.getString("mobileId");
            UUID mobileUuid = UUID.fromString(mobileId);
//            UUID userId = UUID.fromString(authorizationHeader);
            String answer = jsonObj.getString("answer");
            return gameRunningService.checkAnswer(runningGameId, tileUUID, mobileUuid, questionUuid, answer);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/refresh_map/runningGameId={gameId}&userId={mobileId}")
    public Response<MapResponse> refreshMap(@PathVariable(name = "gameId") String gameId, @PathVariable(name = "mobileId") String mobileId,
                                            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            UUID runningGameId = UUID.fromString(gameId);
            Response<List<RunningTile>> tilesResponse = gameRunningService.getRunningTiles(runningGameId);
            int eventIndex = eventService.getUpdatedEventIndex(UUID.fromString(gameId));
            return Response.ok(new MapResponse().setTiles(tilesResponse.getValue()).setEventIndex(eventIndex));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (Exception e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @PostMapping(path = "/end_game")
    public Response<Boolean> endGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("\nRequest received by /end_game endpoint:\n" + jsonObj);
            UUID runningGameId = UUID.fromString(jsonObj.getString("gameId"));
//            UUID hostId = UUID.fromString(authorizationHeader);
            return gameRunningService.endRunningGame(runningGameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/get_lean_running_game_instance/{running_id}")
    @ResponseBody
    public Response<Map<String, Object>> getRunningGameInstanceLean(@PathVariable(name= "running_id") String runningGameId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.getRunningGameInstanceLean(UUID.fromString(runningGameId));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_game_statistic/{running_id}")
    @ResponseBody
    public Response<GameStatistic> getGameStatistic(@PathVariable(name= "running_id") String runningGameId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.getGameStatistic(UUID.fromString(runningGameId));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_all_game_statistics")
    @ResponseBody
    public Response<List<GameStatistic>> getAllGameStatistics(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.getAllGameStatistics();
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/get_player_statistic/{running_id}/{player_id}")
    @ResponseBody
    public Response<PlayerStatistics> getPlayerStatistic(@PathVariable(name= "player_id") String playerId, @PathVariable(name= "running_id") String runningId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.getPlayerStatistic(UUID.fromString(playerId), UUID.fromString(runningId));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_all_player_statistics/{running_id}")
    @ResponseBody
    public Response<List<PlayerStatistics>> getAllPlayersStatistics(@PathVariable(name= "running_id") String runningGameId, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.getAllPlayerStatistics(UUID.fromString(runningGameId));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }
    
    @PostMapping(path = "/report_cheater")
    @ResponseBody
    public Response<Boolean> reportCheater(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("\nRequest received by /report_cheater endpoint:\n" + jsonObj);
            UUID runningGameId = UUID.fromString(jsonObj.getString("gameId"));
            tokenHandler.verifyAnyToken(authorizationHeader);
            return gameRunningService.reportCheater(UUID.fromString(authorizationHeader), runningGameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }
}
