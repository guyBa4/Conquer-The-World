package Application.APILayer.Responses;

import Application.Entities.questions.AssignedQuestion;

public class ValidateAnswerResponse {
    
    private boolean isCorrect;
    private AssignedQuestion nextQuestion;
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public ValidateAnswerResponse setCorrect(boolean correct) {
        isCorrect = correct;
        return this;
    }
    
    public AssignedQuestion getNextQuestion() {
        return nextQuestion;
    }
    
    public ValidateAnswerResponse setNextQuestion(AssignedQuestion nextQuestion) {
        this.nextQuestion = nextQuestion;
        return this;
    }
}
