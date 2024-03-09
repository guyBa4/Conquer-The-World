package Application.Entities;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "questionnaire")
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private UUID Id;

    @Column
    private String name;
    @Transient
    private List<Question> questions;



    public Questionnaire(){

    }

    public Questionnaire(UUID id, String name, List<Question> questionList) {
        Id = id;
        this.name = name;
        this.questions = questionList;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
