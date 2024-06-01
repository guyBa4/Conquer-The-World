package Application.APILayer.controllers;

import Application.Entities.*;
import Application.Repositories.RepositoryFactory;
import Application.Response;
import Application.ServiceLayer.QuestionService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.script.ScriptException;
import java.util.*;
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
            return questionService.addQuestion(question, isMultipleChoice,  correctAnswer,  incorrectAnswers, tags, difficulty);
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
                                                 @RequestParam(required = false) int difficulty,
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
}
