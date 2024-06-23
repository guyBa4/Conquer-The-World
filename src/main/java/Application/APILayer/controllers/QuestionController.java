package Application.APILayer.controllers;

import Application.APILayer.TokenHandler;
import Application.APILayer.controllers.Requests.NewQuestion;
import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.Question;
import Application.Entities.questions.Questionnaire;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@RestController
@RequestMapping("question")
public class QuestionController {
    QuestionService questionService;
    TokenHandler tokenHandler;
    private static Logger LOG;

    @Autowired
    public QuestionController(RepositoryFactory repositoryFactory) throws ScriptException, ExecutionException, InterruptedException {
        this.questionService = QuestionService.getInstance();
        questionService.init(repositoryFactory);
        tokenHandler = TokenHandler.getInstance();
        LOG = getLogger(this.getClass().toString());
    }

    @PostMapping(path = "/add_question")
    @ResponseBody
    public Response<Question> addQuestion(@RequestBody() NewQuestion newQuestion, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            if (newQuestion != null)
                return questionService.addQuestion(newQuestion);
            return Response.fail(400, "Invalid request");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/add_questions")
    @ResponseBody
    public Response<Question> addQuestion(@RequestBody() List<NewQuestion> newQuestions, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            if (newQuestions != null)
                for (NewQuestion newQuestion : newQuestions)
                    return questionService.addQuestion(newQuestion);
            return Response.fail(400, "Invalid request");
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @PostMapping(path = "/add_questionnaire")
    @ResponseBody
    public Response<Questionnaire> addQuestionnaire(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
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
    public Response<List<AssignedQuestion>> getQuestionsByQuestionnaireId(@PathVariable (name= "questionnaireId") UUID questionnaireId,
                                                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            return questionService.getQuestionsByQuestionnaireId(questionnaireId);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @GetMapping(path = "/filter_questions")
    public Response<Page<Question>> getQuestions(@RequestParam int page,
                                                 @RequestParam int size,
                                                 @RequestParam(required = false) Integer difficulty,
                                                 @RequestParam(required = false) String content,
                                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader){
//            ,                                                 @RequestParam(name = "tags", required = false) List<String> tags) {

        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
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
                                                           @RequestParam(name = "creator_id", required = false) UUID creatorId,
                                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
            List<String> tags = new ArrayList<>();
            return questionService.filterQuestionnaires(page, size, name, tags, creatorId);
        } catch (JSONException e) {
            return Response.fail(500, "Internal Server Error"); // Internal Server Error
        }
    }

    @DeleteMapping(path = "/delete_question")
    public Response<Boolean> deleteQuestion(@RequestBody String inputJson, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            tokenHandler.verifyAnyToken(authorizationHeader);
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
            tokenHandler.verifyAnyToken(authorizationHeader);
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
