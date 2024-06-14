package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.*;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.GameRunningService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@RestController
@RequestMapping(path = "running_game")
public class RunningGameController {

    TokenHandler tokenHandler;
    GameRunningService gameRunningService;
    private static Logger LOG;
    
    @Autowired
    public RunningGameController(RepositoryFactory repositoryFactory, GameRunningService gameRunningService)
    {
        this.gameRunningService = gameRunningService;
        tokenHandler = TokenHandler.getInstance();
        LOG = getLogger(this.getClass().toString());
    }

    @PostMapping(path = "/open_waiting_room")
    @ResponseBody
    public Response<RunningGameInstance> openWaitingRoom(@RequestBody String inputJson) {
        try {
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
            return gameRunningService.enterGameWithCode(gameCode);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/enter_player_details")
    @ResponseBody
    public Response<RunningGameInstance> addMobileDetails(@RequestBody String inputJson) {
        try {
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
    public Response<RunningGameInstance> getWaitingRoomDetails(@PathVariable (name= "game_id") String gameId) {
        try {
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
            LOG.info(String.format("Request received by /generate_question endpoint:\n {'group': %s, 'runningGameId': %s, 'runningTileId': %s}", group, runningGameId, runningTileId));
            return gameRunningService.getQuestion(UUID.fromString(runningTileId), group, UUID.fromString(runningGameId), UUID.fromString(authorizationHeader));
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/validate_answer")
    public Response<Boolean> validateAnswer(@RequestBody String inputJson){
//                                            ,@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
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
    public Response<List<RunningTile>> refreshMap(@PathVariable(name = "gameId") String gameId, @PathVariable(name = "mobileId") String mobileId) {
        try {
            UUID runningGameId = UUID.fromString(gameId);
//            UUID mobileUuid = UUID.fromString(mobileId);
            return gameRunningService.getRunningTiles(runningGameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @PostMapping(path = "/end_game")
    public Response<Boolean> endGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /end_game endpoint:\n" + jsonObj);
            UUID runningGameId = UUID.fromString(jsonObj.getString("gameId"));
//            UUID hostId = UUID.fromString(authorizationHeader);
            return gameRunningService.endRunningGame(runningGameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.getMessage());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

}
