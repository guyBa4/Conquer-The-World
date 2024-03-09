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
    @Column
    private String Type;
    @Column
    private String question;
    @Column
    private String answer;
    @Column
    private Integer difficulty;

//    private List<String> tags;


    public Question(){

    }

    public Question(UUID id, String type, String question, String answer, int difficulty) {
        Id = id;
//        Type = type;
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
