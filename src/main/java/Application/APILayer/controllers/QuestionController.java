package Application.APILayer.controllers;

import Application.Entities.Map;
import Application.Entities.Question;
import Application.Entities.User;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.QuestionService;
import Application.ServiceLayer.UserService;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("question")
public class QuestionController {
    QuestionService questionService;

    @Autowired
    public QuestionController(RepositoryFactory repositoryFactory) throws ScriptException, ExecutionException, InterruptedException {
        this.questionService = QuestionService.getInstance();
        questionService.init(repositoryFactory);
        System.out.println("aaaaaaaaaaaaa");

    }

    @PostMapping(path = "/add_question")
    @ResponseBody
    public Response<Question> addQuestion(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            Response<Question> response = questionService.addQuestion(jsonObj);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
