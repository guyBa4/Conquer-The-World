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

@RestController
@RequestMapping(path = "running_game")
public class RunningGameController {

    TokenHandler tokenHandler;
    GameRunningService gameRunningService;

    @Autowired
    public RunningGameController(RepositoryFactory repositoryFactory)
    {
        this.gameRunningService = GameRunningService.getInstance();
        if (gameRunningService.getRunningGamesIdToRunningGameInstance() == null)
            gameRunningService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
    }

    @PostMapping(path = "/open_waiting_room")
    @ResponseBody
    public Response<RunningGameInstance> OpenWaitingRoom(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
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
    public Response<RunningGameInstance> getRunningGame(@PathVariable (name= "runningGameUuid") String runningGameid,
                                                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            UUID runningGameUuid = UUID.fromString(runningGameid);
            return gameRunningService.getRunningGame(runningGameUuid);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }
// hi

    @GetMapping(path = "/generate_question/{difficulty}&{runningGameid}")
    public Response<Question> getQuestion(@PathVariable (name= "difficulty") int difficulty, @PathVariable (name= "runningGameid") String runningGameid,
                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            return gameRunningService.getQuestion(difficulty, runningGameid);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


}
