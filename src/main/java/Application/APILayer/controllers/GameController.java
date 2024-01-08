package Application.APILayer.controllers;

import Application.Entities.GameInstance;
import Application.Entities.Question;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.GameService;
import Application.ServiceLayer.QuestionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "game")
public class GameController {
    GameService gameService;

    @Autowired
    public GameController(RepositoryFactory repositoryFactory)
    {
        this.gameService = GameService.getInstance();
        gameService.init(repositoryFactory);
    }

    @PostMapping(path = "/add_game_instance")
    @ResponseBody
    public Response<GameInstance> addGameInstance(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            Response<GameInstance> response = gameService.addGameInstance(jsonObj);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }


    @PostMapping(path = "/jsonExample")
    @ResponseBody
    public ResponseEntity<String> jsonTalking(@RequestBody String inputJson) {
        try {
            Question q = new Question();
            JSONObject jsonObj = new JSONObject(inputJson);
            String message = jsonObj.get(jsonObj.keys().next().toString()).toString();
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", 200);
            responseJson.put("message", message);
            return ResponseEntity.ok(responseJson.toString());
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        }
    }


    @GetMapping(path = "/hello")
    public String register() {
        return "hello world";
    }
}
