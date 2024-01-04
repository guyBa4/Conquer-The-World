package Application.APILayer;

import Application.Entities.Question;
import Application.Repositories.QuestionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "game")
public class GameController {
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
