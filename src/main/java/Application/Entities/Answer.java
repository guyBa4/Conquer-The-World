package Application.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "answers")
public class Answer {
    
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "answer_text")
    private String answerText;
    
    @Column(name = "correct")
    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    public Answer(){}

    public Answer(String answerText, boolean correct) {
        this.answerText = answerText;
        this.correct = correct;
    }

    public Answer(String answerText, boolean correct, Question question) {
        this.answerText = answerText;
        this.correct = correct;
        this.question = question;
    }



    public UUID getId() {
        return id;
    }

    public Answer setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getAnswerText() {
        return answerText;
    }

    public Answer setAnswerText(String answerText) {
        this.answerText = answerText;
        return this;
    }

    public boolean getCorrect() {
        return correct;
    }

    public Answer setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }

    public Question getQuestion() {
        return question;
    }

    public Answer setQuestion(Question question) {
        this.question = question;
        return this;
    }
}
