package Application.APILayer.controllers;

import Application.Entities.*;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.QuestionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@RestController
@RequestMapping("question")
public class QuestionController {
    QuestionService questionService;
    private static Logger LOG;

    @Autowired
    public QuestionController(RepositoryFactory repositoryFactory) throws ScriptException, ExecutionException, InterruptedException {
        this.questionService = QuestionService.getInstance();
        questionService.init(repositoryFactory);
        LOG = getLogger(this.getClass().toString());
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
            byte[] image = null;
            if (jsonObj.getString("image") != null) {
                image = jsonObj.getString("image").getBytes(StandardCharsets.UTF_8);
            }
            return questionService.addQuestion(question, isMultipleChoice, correctAnswer, incorrectAnswers, tags, difficulty, image);
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
            Map<String, Object> questionsIdsToDifficulty = questionsIdsObject.toMap();
            return questionService.addQuestionnaire(title, creatorId, questionsIdsToDifficulty);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/get_questions/questionnaireId={questionnaireId}")
    @ResponseBody
    public Response<List<AssignedQuestion>> getQuestionsByQuestionnaireId(@PathVariable (name= "questionnaireId") UUID questionnaireId) {
        try {
            return questionService.getQuestionsByQuestionnaireId(questionnaireId);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/filter_questions")
    public Response<Page<Question>> getQuestions(@RequestParam int page,
                                                 @RequestParam int size,
                                                 @RequestParam(required = false) Integer difficulty,
                                                 @RequestParam(required = false) String content){
//            ,                                                 @RequestParam(name = "tags", required = false) List<String> tags) {

        try {
            List<String> tags = new LinkedList<>();
            return questionService.filterQuestions(page, size, content, tags, difficulty);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }
    
    @GetMapping(path = "/filter_questionnaires")
    public Response<Page<Questionnaire>> getQuestionnaires(@RequestParam int page,
                                                           @RequestParam int size,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(name = "creator_id", required = false) UUID creatorId) {
        try {
            List<String> tags = new ArrayList<>();
            return questionService.filterQuestionnaires(page, size, name, tags, creatorId);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @DeleteMapping(path = "/delete_question")
    public Response<Boolean> deleteQuestion(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by delete_question endpoint:\n" + jsonObj);
            UUID id = UUID.fromString(jsonObj.getString("id"));
            return questionService.deleteQuestion(id);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

    @DeleteMapping(path = "/delete_questionnaire")
    public Response<Boolean> deleteQuestionnaire(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            LOG.info("Request received by delete_questionnaire endpoint:\n" + jsonObj);
            UUID id = UUID.fromString(jsonObj.getString("id"));
            return questionService.deleteQuestionnaire(id);
        } catch (IllegalArgumentException e) {
            return Response.fail(403, e.toString());
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error");
        }
    }

}
