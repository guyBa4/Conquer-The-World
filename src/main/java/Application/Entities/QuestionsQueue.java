package Application.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

@Entity
@Table(name = "questions_queue")
public class QuestionsQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "assigned_questions_to_assigned_questions_queues",
            joinColumns = @JoinColumn(name = "assigned_question_id"),
            inverseJoinColumns = @JoinColumn(name = "queue_id")
    )
    @JsonIgnore
    private List<AssignedQuestion> questionsQueues;

    private int difficulty;

    public QuestionsQueue(){
        questionsQueues = new LinkedList<>();
    }

    public QuestionsQueue(int difficulty, List<AssignedQuestion> assignedQuestions) {
        this.difficulty = difficulty;
        this.questionsQueues = this.RandomOrder(assignedQuestions);
    }

    private List<AssignedQuestion> RandomOrder(List<AssignedQuestion> assignedQuestions) {
        return shuffleList(assignedQuestions);
    }
    public static <T> List<T> shuffleList(List<T> inputList) {
        Collections.shuffle(inputList);
        return inputList;
    }

}
