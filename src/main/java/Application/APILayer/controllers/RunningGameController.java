package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.GameInstance;
import Application.Entities.MobilePlayer;
import Application.Entities.Question;
import Application.Entities.RunningGameInstance;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.GameRunningService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
    public RunningGameController(RepositoryFactory repositoryFactory)
    {
        this.gameRunningService = GameRunningService.getInstance();
        if (gameRunningService.getRunningGamesIdToRunningGameInstance() == null)
            gameRunningService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
        LOG = getLogger(this.getClass().toString());
    }

    @PostMapping(path = "/open_waiting_room")
    @ResponseBody
    public Response<RunningGameInstance> OpenWaitingRoom(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
//            LOG.info("Request received by /open_waiting_room endpoint:\n" + jsonObj);
            UUID gameId = UUID.fromString(jsonObj.getString("gameId"));
            UUID userId = UUID.fromString(jsonObj.getString("userId"));
            return gameRunningService.OpenWaitingRoom(gameId, userId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/enter_game_code")
    @ResponseBody
    public Response<UUID> enterGame(@RequestBody String inputJson) {
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
    public Response<GameInstance> addMobileDetails(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
//            LOG.info("Request received by /enter_player_details endpoint:\n" + jsonObj);
//            JSONObject jsonAuthorizationHeader = new JSONObject(authorizationHeader);
            String name = jsonObj.getString("name");
            UUID mobileId = UUID.fromString(authorizationHeader);
            return gameRunningService.addMobileDetails(mobileId, name);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }

    }

    @GetMapping(path = "/get_waiting_room/{game_id}")
    public Response<RunningGameInstance> getWaitingRoomDetails(@PathVariable (name= "game_id") String gameId,
                                                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            UUID gameUuid = UUID.fromString(gameId);
            LOG.info("Request received by /get_waiting_room endpoint:\n{'gameId': " + gameId + "}");
            return gameRunningService.getWaitingRoomDetails(gameUuid);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/start_game")
    @ResponseBody
    public Response<RunningGameInstance> startGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /start_game endpoint:\n " + jsonObj);
            UUID gameId = UUID.fromString(jsonObj.getString("gameId"));
            return gameRunningService.startGame(gameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/get_running_game/{runningGameid}")
    public Response<RunningGameInstance> getRunningGame(@PathVariable (name= "runningGameUuid") String runningGameId,
                                                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            UUID runningGameUuid = UUID.fromString(runningGameId);
            LOG.info("Request received by /get_running_game endpoint:\n " + runningGameId);
            return gameRunningService.getRunningGame(runningGameUuid);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/generate_question/{difficulty}&{runningGameid}")
    public Response<Question> getQuestion(@PathVariable (name= "difficulty") int difficulty, @PathVariable (name= "runningGameid") String runningGameId,
                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            LOG.info(String.format("Request received by /generate_question endpoint:\n {'difficulty': %s, 'runningGameId': %s}", difficulty, runningGameId));
            return gameRunningService.getQuestion(difficulty, runningGameId);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @PostMapping(path = "/validate_answer")
    public Response<Boolean> validateAnswer(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by /open_waiting_room endpoint:\n" + jsonObj);
            UUID runningGameid = UUID.fromString(jsonObj.getString("gameId"));
            UUID questionUuid = UUID.fromString(jsonObj.getString("questionId"));
            String answer = jsonObj.getString("answer");
            return gameRunningService.checkAnswer(runningGameid, questionUuid, answer);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

}
