package Application.Entities.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "questions_queue")
public class QuestionsQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "assigned_questions_to_assigned_questions_queues",
            joinColumns = @JoinColumn(name = "assigned_question_id"),
            inverseJoinColumns = @JoinColumn(name = "queue_id")
    )
    @JsonIgnore
    private List<AssignedQuestion> questionsQueue;

    private int difficulty;

    public QuestionsQueue(){
        questionsQueue = new LinkedList<>();
    }

    public QuestionsQueue(int difficulty, List<AssignedQuestion> assignedQuestions) {
        this.difficulty = difficulty;
        this.questionsQueue = assignedQuestions;
    }

//    private List<AssignedQuestion> RandomOrder(List<AssignedQuestion> assignedQuestions) {
//        return shuffleList(assignedQuestions);
//    }
    public static <T> List<T> shuffleList(List<T> inputList) {
        Collections.shuffle(inputList);
        return inputList;
    }

    public UUID getId() {
        return id;
    }

    public QuestionsQueue setId(UUID id) {
        this.id = id;
        return this;
    }

    public List<AssignedQuestion> getQuestionsQueue() {
        return questionsQueue;
    }

    public QuestionsQueue setQuestionsQueue(List<AssignedQuestion> questionsQueue) {
        this.questionsQueue = questionsQueue;
        return this;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public QuestionsQueue setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public AssignedQuestion generateQuestionFromQueue() {
        int i = (int) (Math.random()*(this.questionsQueue.size()));
        return questionsQueue.remove(i);
    }
}
