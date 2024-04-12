package Application.Entities;

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
    
//    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "question_id")
//    private Question question;

    public Answer(){}
    public Answer(String answerText, boolean correct) {
        this.answerText = answerText;
        this.correct = correct;
//        this.question = question;
    }
//    public Answer(String answerText, boolean correct) {
//        this.answerText = answerText;
//        this.correct = correct;
//    }


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

    public boolean isCorrect() {
        return correct;
    }

    public Answer setCorrect(boolean correct) {
        this.correct = correct;
        return this;
    }

//    public Question getQuestion() {
//        return question;
//    }
//
//    public Answer setQuestion(Question question) {
//        this.question = question;
//        return this;
//    }
}
