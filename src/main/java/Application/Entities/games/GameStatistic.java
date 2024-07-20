package Application.Entities.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "game_statistics")
public class GameStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_started")
    private Date timeStarted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_ended")
    private Date timeEnded;

    @Column(name = "questions_answered")
    private int questionsAnswered;

    @Column(name = "correct_answers")
    private int correctAnswers;

    @OneToOne
    @JoinColumn(name = "running_game_instance_id", nullable = false)
    @JsonIgnore
    private RunningGameInstance runningGameInstance;

    public GameStatistic() {
    }

    public GameStatistic(RunningGameInstance runningGameInstance) {
        this.runningGameInstance = runningGameInstance;
        this.timeStarted = new Time(new Date().getTime());
        this.timeEnded = timeStarted;
    }

    public UUID getId() {
        return id;
    }

    public GameStatistic setId(UUID id) {
        this.id = id;
        return this;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public GameStatistic setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
        return this;
    }

    public Date getTimeEnded() {
        return timeEnded;
    }

    public GameStatistic setTimeEnded(Date timeEnded) {
        this.timeEnded = timeEnded;
        return this;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public GameStatistic setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
        return this;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public GameStatistic setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
        return this;
    }

    public RunningGameInstance getRunningGameInstance() {
        return runningGameInstance;
    }

    public GameStatistic setRunningGameInstance(RunningGameInstance runningGameInstance) {
        this.runningGameInstance = runningGameInstance;
        return this;
    }
    public void addQuestionsAnswered(){
        this.questionsAnswered++;
    }
    public void addCorrectAnswers(){
        this.correctAnswers++;
    }
}
