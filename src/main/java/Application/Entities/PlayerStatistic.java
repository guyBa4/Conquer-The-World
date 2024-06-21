package Application.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "player_statistics")
public class PlayerStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "score")
    private int score;

    @Column(name = "questions_answered")
    private int questionsAnswered;

    @Column(name = "correct_answers")
    private int correctAnswers;

    @ManyToOne
    @JoinColumn(name = "mobile_player_id", nullable = false)
    private MobilePlayer mobilePlayer;

    @ManyToOne
    @JoinColumn(name = "running_game_instance_id", nullable = false)
    private RunningGameInstance runningGameInstance;

    public PlayerStatistic(int score, int questionsAnswered, int correctAnswers, MobilePlayer mobilePlayer) {
        this.score = score;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.mobilePlayer = mobilePlayer;
    }
    public PlayerStatistic(MobilePlayer mobilePlayer, RunningGameInstance runningGameInstance) {
        this.score = 0;
        this.questionsAnswered = 0;
        this.correctAnswers = 0;
        this.mobilePlayer = mobilePlayer;
        this.runningGameInstance = runningGameInstance;
    }

    public PlayerStatistic(){}

    public int getScore() {
        return score;
    }

    public PlayerStatistic setScore(int score) {
        this.score = score;
        return this;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public PlayerStatistic setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
        return this;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public PlayerStatistic setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
        return this;
    }

    public void addQuestionsAnswered(){
        this.questionsAnswered++;
    }
    public void addCorrectAnswers(){
        this.correctAnswers++;
    }
    public void addScore(int score){
        this.score+=score;
    }
}
