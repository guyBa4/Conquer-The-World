package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.GameInstance;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.GameRunningService;
import Application.ServiceLayer.GameService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;
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
        gameRunningService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
    }

    @PostMapping(path = "/start_game")
    @ResponseBody
    public Response<GameInstance> startGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            UUID gameId = UUID.fromString(jsonObj.getString("gameId"));
            UUID userId = UUID.fromString(jsonObj.getString("userId"));
            return gameRunningService.startGame(gameId, userId);
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
            String name = jsonObj.getString("name");
            UUID mobileId = UUID.fromString(jsonObj.getString("mobileId"));
            return gameRunningService.addMobileDetails(mobileId, name);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it appropriately
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }

    }

}
