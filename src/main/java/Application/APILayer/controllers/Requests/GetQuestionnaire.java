package Application.APILayer.controllers.Requests;

import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.Questionnaire;
import Application.Entities.users.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GetQuestionnaire {
    private UUID id;
    private String name;
    private User creator;
    private List<String> questions;
    private List<String> tags;

    public GetQuestionnaire(UUID id, String name, User creator, List<String> questions, List<String> tags) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.questions = questions;
        this.tags = tags;
    }
    public GetQuestionnaire(Questionnaire questionnaire) {
        this.id = questionnaire.getId();
        this.name = questionnaire.getName();
        this.creator = questionnaire.getCreator();
        this.questions = new LinkedList<>();
        for (AssignedQuestion assignedQuestion : questionnaire.getQuestions())
            questions.add(assignedQuestion.getQuestion().getQuestion());
        this.tags = questionnaire.getTags().stream().toList();
    }

    public UUID getId() {
        return id;
    }

    public GetQuestionnaire setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GetQuestionnaire setName(String name) {
        this.name = name;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public GetQuestionnaire setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public GetQuestionnaire setQuestions(List<String> questions) {
        this.questions = questions;
        return this;
    }

//    public Set<String> getTags() {
//        return tags;
//    }
//
//    public GetQuestionnaire setTags(Set<String> tags) {
//        this.tags = tags;
//        return this;
//    }
}
