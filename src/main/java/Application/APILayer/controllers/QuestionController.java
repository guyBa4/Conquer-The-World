package Application.APILayer.controllers;

import Application.Entities.*;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.QuestionService;
import Application.ServiceLayer.UserService;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    }

    @PostMapping(path = "/add_question")
    @ResponseBody
    public Response<Question> addQuestion(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            String question = jsonObj.getString("question");
            boolean isMultipleChoice = jsonObj.getBoolean("isMultipleChoice");
            String correctAnswer = jsonObj.getString("correctAnswer");
            List<Object> incorrectAnswers = jsonObj.getJSONArray("incorrectAnswers").toList();
            List<Object> tags = jsonObj.getJSONArray("tags").toList();
            int difficulty = jsonObj.getInt("difficulty");
            Response<Question> response = questionService.addQuestion(question, isMultipleChoice,  correctAnswer,  incorrectAnswers, tags, difficulty);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/add_questionnaire")
    @ResponseBody
    public Response<Questionnaire> addQuestionnaire(@RequestBody String inputJson) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            String title = jsonObj.getString("title");
            String creatorId = jsonObj.getString("creatorId");
            JSONObject questionsIdsObject = jsonObj.getJSONObject("questionsIds");
            java.util.Map questionsIdsToDifficulty = questionsIdsObject.toMap();
            Response<Questionnaire> response = questionService.addQuestionnaire(title, creatorId, questionsIdsToDifficulty);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/get_questions/questionnaireId={questionnaireId}")
    @ResponseBody
    public Response<List<AssignedQuestion>> getQuestionsByQuestionnaireId(@PathVariable (name= "questionnaireId") UUID questionnaireId) {
        try {
            Response<List<AssignedQuestion>> response = questionService.getQuestionsByQuestionnaireId(questionnaireId);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/filter_questions/page={page}&size={size}&content={content}&difficulty={difficulty}")
    public Response<Page<Question>> getQuestions(@PathVariable(name = "page") int page,
                                                 @PathVariable(name = "size") int size,
                                                 @PathVariable(name = "difficulty") Integer difficulty,
                                                 @PathVariable(name = "content") String content){
//            ,                                                 @RequestParam(name = "tags", required = false) List<String> tags) {

        try {
            List<String> tags = new LinkedList<>();
            Response<Page<Question>> response = questionService.filterQuestions(page, size, content, tags, difficulty);
            return response;
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
}
