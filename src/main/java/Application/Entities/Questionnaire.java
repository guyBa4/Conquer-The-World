package Application.Entities;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "questionnaire")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssignedQuestion> questions;

    @Column(name = "time_created")
    private Time timeCreated;

    @Column(name = "last_updated")
    private Time lastUpdated;

    @Column(name = "shared")
    private boolean shared;


    public Questionnaire(){

    }

    public Questionnaire(UUID id, String name, List<AssignedQuestion> questionList) {
        uuid = id;
        this.name = name;
        this.questions = questionList;
    }

    public UUID getId() {
        return uuid;
    }

    public void setId(UUID uuid) {
        uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssignedQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssignedQuestion> questions) {
        this.questions = questions;
    }

}
