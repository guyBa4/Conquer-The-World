package Application.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    
    private boolean multipleChoice;
    
    @Column
    private String question;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @Column
    private Integer difficulty;
    
    @Lob
    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image; // Holds image data

//    @Transient
//    private List<String> tags;


    public Question(){

    }


    public Question(boolean multipleChoice, String question, List<Answer> answers, Integer difficulty) {
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.answers = answers;
        this.difficulty = difficulty;
    }
    
    public Question(boolean multipleChoice, String question, Integer difficulty, byte[] image) {
        this.multipleChoice = multipleChoice;
        this.question = question;
        this.difficulty = difficulty;
        this.image = image;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
                return ans.getCorrect();
        }
        return false;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public Question setImage(byte[] image) {
        this.image = image;
        return this;
    }
    
    //    public String[] getTags() {
//        return tags;
//    }
//
//    public void setTags(String[] tags) {
//        this.tags = tags;
//    }
}
