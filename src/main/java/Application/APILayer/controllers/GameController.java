package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.GameInstance;
import Application.Entities.Question;
import Application.Entities.User;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.FlatGameInstance;
import Application.ServiceLayer.GameService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "game")
public class GameController {
    GameService gameService;
    TokenHandler tokenHandler;
    @Autowired
    public GameController(RepositoryFactory repositoryFactory)
    {
        this.gameService = GameService.getInstance();
        gameService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
    }

    @PostMapping(path = "/add_game_instance")
    @ResponseBody
    public Response<GameInstance> addGameInstance(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
//            tokenHandler.verifyWebUserToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            Response<GameInstance> response = gameService.addGameInstance(jsonObj);
            return response;
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/get_all_games")
    @ResponseBody
    public Response<List<GameInstance>> GetGamesInstances() {
        try {
            Response<List<GameInstance>> response = gameService.getAllGameInstance();
            return response;
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_game/id={id}")
    @ResponseBody
    public Response<GameInstance> GetGamesInstances(@PathVariable (name= "id") UUID id) {
        try {
            return gameService.getGameInstance(id);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, "AUTHORIZATION FAILED");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


}
