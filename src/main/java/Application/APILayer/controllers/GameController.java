package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.Entities.GameInstance;
import Application.Entities.GameMap;
import Application.Entities.Question;
import Application.Entities.User;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.FlatGameInstance;
import Application.ServiceLayer.GameService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@RestController
@RequestMapping(path = "game")
public class GameController {
    GameService gameService;
    TokenHandler tokenHandler;
    private static Logger LOG;
    @Autowired
    public GameController(RepositoryFactory repositoryFactory)
    {
        this.gameService = GameService.getInstance();
        gameService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
        LOG = getLogger(this.getClass().toString());
    }

    @PostMapping(path = "/add_game_instance")
    @ResponseBody
    public Response<GameInstance> addGameInstance(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
//            tokenHandler.verifyWebUserToken(authorizationHeader);
            JSONObject jsonObj = new JSONObject(inputJson);
            String title = jsonObj.getString("title");
            String description = jsonObj.getString("description");
            String questionnaireId = jsonObj.getString("questionnaireID");
            UUID questionnaireUuid = UUID.fromString(questionnaireId);
            String mapId = jsonObj.getString("mapId");
            UUID mapUuid = UUID.fromString(mapId);
            String creatorId = jsonObj.getString("creatorId");
            UUID creatorUuid = UUID.fromString(creatorId);
            int groups = jsonObj.getInt("groups");
            int gameTime = jsonObj.getInt("gameTime");
            boolean isShared = jsonObj.getBoolean("isShared");
            int questionTimeLimit = jsonObj.getInt("questionTimeLimit");
//            List<Object> startingPositions = jsonObj.getJSONArray("startingPositions").toList();
            return gameService.addGameInstance(title, description, questionnaireUuid, mapUuid,creatorUuid, groups, gameTime, isShared, questionTimeLimit);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }


    @GetMapping(path = "/get_all_games")
    @ResponseBody
    public Response<List<GameInstance>> GetGamesInstances() {
        try {
            return gameService.getAllGameInstance();
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_game")
    @ResponseBody
    public Response<GameInstance> getGamesInstance(@RequestParam UUID id) {
        try {
            return gameService.getGameInstance(id);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @GetMapping(path = "/get_maps")
    @ResponseBody
    public Response<Page<GameMap>> getMaps(@RequestParam int page,
                                           @RequestParam int size,
                                           @RequestParam(required = false) String name){
        try {
            return gameService.getMaps(page, size, name);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @DeleteMapping(path = "/delete_game")
    public Response<Boolean> deleteGame(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by delete_game endpoint:\n" + jsonObj);
            UUID id = UUID.fromString(jsonObj.getString("id"));
//            UUID hostId = UUID.fromString(authorizationHeader);
            return gameService.deleteGame(id);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

}
