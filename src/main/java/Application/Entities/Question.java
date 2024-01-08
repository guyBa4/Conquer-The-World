package Application.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private Long Id;
//    @Column
//    private String Type;
    @Column
    private String question;
    @Column
    private String answer;
    @Column
    private Integer difficulty;


    public Question(){

    }

    public Question(Long id, String type, String question, String answer, int difficulty) {
        Id = id;
//        Type = type;
        this.question = question;
        this.answer = answer;
        this.difficulty = difficulty;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

//    public String getType() {
//        return Type;
//    }
//
//    public void setType(String type) {
//        Type = type;
//    }

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
