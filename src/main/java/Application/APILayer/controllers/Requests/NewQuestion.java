package Application.APILayer.controllers.Requests;

import java.util.List;

public class NewQuestion {
    private String question;
    private boolean isMultipleChoice;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private List<String> tags;
    private int difficulty;
    private String image;

    public boolean isShared() {
        return shared;
    }

    public NewQuestion setShared(boolean shared) {
        this.shared = shared;
        return this;
    }

    private boolean shared;

    public String getQuestion() {
        return question;
    }
    
    public NewQuestion setQuestion(String question) {
        this.question = question;
        return this;
    }
    
    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }
    
    public NewQuestion setMultipleChoice(boolean multipleChoice) {
        isMultipleChoice = multipleChoice;
        return this;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public NewQuestion setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }
    
    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }
    
    public NewQuestion setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
        return this;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public NewQuestion setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public NewQuestion setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        return this;
    }
    
    public String getImage() {
        return image;
    }
    
    public NewQuestion setImage(String image) {
        this.image = image;
        return this;
    }
}
