package Application.Entities.users;

import Application.Entities.games.RunningGameInstance;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "player_statistics")
public class PlayerStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "score")
    private int score;

    @Column(name = "questions_answered")
    private int questionsAnswered;

    @Column(name = "correct_answers")
    private int correctAnswers;
    
    @Column(name = "cheated?")
    private boolean cheated;

    @OneToOne
    @JoinColumn(name = "mobile_player_id", nullable = false)
    private MobilePlayer mobilePlayer;

    @ManyToOne
    @JoinColumn(name = "running_game_instance_id", nullable = false)
    private RunningGameInstance runningGameInstance;

    public PlayerStatistics(int score, int questionsAnswered, int correctAnswers, MobilePlayer mobilePlayer) {
        this.score = score;
        this.questionsAnswered = questionsAnswered;
        this.correctAnswers = correctAnswers;
        this.mobilePlayer = mobilePlayer;
    }
    public PlayerStatistics(MobilePlayer mobilePlayer, RunningGameInstance runningGameInstance) {
        this.score = 0;
        this.questionsAnswered = 0;
        this.correctAnswers = 0;
        this.mobilePlayer = mobilePlayer;
        this.runningGameInstance = runningGameInstance;
    }

    public PlayerStatistics(){}

    public int getScore() {
        return score;
    }

    public PlayerStatistics setScore(int score) {
        this.score = score;
        return this;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public PlayerStatistics setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
        return this;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public PlayerStatistics setCorrectAnswers(int correctAnswers) {
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
    
    public boolean getCheated() {
        return cheated;
    }
    
    public PlayerStatistics setCheated(boolean cheated) {
        this.cheated = cheated;
        return this;
    }
}
