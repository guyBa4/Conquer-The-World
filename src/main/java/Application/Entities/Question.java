package Application.Entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private UUID Id;
    private boolean multipleChoice;
    @Column
    private String question;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private List<Answer> answers;

    @Column
    private Integer difficulty;

    //    @Transient
//    private List<String> tags;


    public Question(){

    }


    public Question(UUID id, boolean multipleChoice, String question, List<Answer> answer, String[] incorrectAnswers, Integer difficulty, String[] tags) {
        Id = id;
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.answers = answer;
//        this.incorrectAnswers = incorrectAnswers;
        this.difficulty = difficulty;
//        this.tags = tags;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
    public Answer getAnswer(String answerInput) {
        for(Answer ans : answers){
            if (ans.getAnswerText().equals(answerInput))
                return ans;
        }
        return null;
    }
    public boolean isAnswerCorrect(String answerInput) {
        for(Answer ans : answers){
            if (ans.getAnswerText().equals(answerInput))
                return ans.isCorrect();
        }
        return false;
    }

    public void setAnswers(String answers) {
        answers = answers;
    }

//    public String[] getIncorrectAnswers() {
//        return incorrectAnswers;
//    }
//
//    public void setIncorrectAnswers(String[] incorrectAnswers) {
//        this.incorrectAnswers = incorrectAnswers;
//    }
//
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

//    public String[] getTags() {
//        return tags;
//    }
//
//    public void setTags(String[] tags) {
//        this.tags = tags;
//    }
}
