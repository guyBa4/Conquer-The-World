package Application.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "assigned_questions")
@Entity
public class AssignedQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;
    
    @Column(name = "difficulty_level")
    private int difficultyLevel;

    public AssignedQuestion(Question question) {
        this.question = question;
    }
    public AssignedQuestion() {
    }

    public UUID getId() {
        return id;
    }

    public AssignedQuestion setId(UUID id) {
        this.id = id;
        return this;
    }

    public Question getQuestion() {
        return question;
    }

    public AssignedQuestion setQuestion(Question question) {
        this.question = question;
        return this;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public AssignedQuestion setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        return this;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public AssignedQuestion setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        return this;
    }
}
