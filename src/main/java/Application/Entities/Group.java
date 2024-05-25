package Application.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private List<MobilePlayer> mobilePlayers;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private RunningGameInstance runningGameInstance;

    @Column(name = "group_number")
    private int number;
    @Column(name = "score")
    private int score;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "questionsQueues_id")
    private List<QuestionsQueue> questionsQueues;

    public Group(){
    }

    public Group(int number, RunningGameInstance runningGameInstance){
        this.number = number;
        this.score = 0;
        this.mobilePlayers = new LinkedList<>();
        this.runningGameInstance = runningGameInstance;
        this.questionsQueues = new LinkedList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RunningGameInstance getRunningGameInstance() {
        return runningGameInstance;
    }

    public void setRunningGameInstance(RunningGameInstance runningGameInstance) {
        this.runningGameInstance = runningGameInstance;
    }

    public List<MobilePlayer> getMobilePlayers() {
        return mobilePlayers;
    }

    public void setMobilePlayers(List<MobilePlayer> mobilePlayers) {
        this.mobilePlayers = mobilePlayers;
    }
    public void addMobilePlayer(MobilePlayer mobilePlayer) {
        this.mobilePlayers.add(mobilePlayer);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public void addScore(int score) {
        this.score+=score;
    }

    public List<QuestionsQueue> getQuestionsQueues() {
        return questionsQueues;
    }
    public void addQuestionQueue(int difficulty, List<AssignedQuestion> assignedQuestions){
        questionsQueues.add(new QuestionsQueue(difficulty, assignedQuestions));
    }

    public void setQuestionsQueues(List<QuestionsQueue> questionsQueues) {
        this.questionsQueues = questionsQueues;
    }

    public int getSize(){
        return mobilePlayers.size();
    }

    public AssignedQuestion generateQuestionFromQueue(int difficultyLevel) {
        for(QuestionsQueue queue : this.questionsQueues){
            if (queue.getDifficulty() == difficultyLevel){
                AssignedQuestion assignedQuestion = queue.generateQuestionFromQueue();
                return assignedQuestion;
            }
        }
        return null;
    }
}
