package Application.Entities;
import jakarta.persistence.*;

import java.util.*;
import java.util.Map;

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
    private Map<Integer, List<Question>> questionMap;



    public Questionnaire(){

    }

    public Questionnaire(UUID id, String name, List<Question> questionList) {
        Id = id;
        this.name = name;
        this.questionMap = new HashMap<>();
        for (Question question : questionList) {
            int difficulty  = question.getDifficulty();
            this.questionMap.putIfAbsent(difficulty, new LinkedList<>());
            questionMap.get(difficulty).add(question);
        }
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

    public Map<Integer, List<Question>> getQuestionMap() {
        return questionMap;
    }

    public void setQuestionMap(Map<Integer, List<Question>> questionMap) {
        this.questionMap = questionMap;
    }

    public List<Question> getQuestionsByDufficulty(int difficulty){
        return questionMap.get(difficulty);
    }
}
