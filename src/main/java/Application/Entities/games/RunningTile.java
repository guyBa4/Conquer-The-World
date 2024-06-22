package Application.Entities.games;

import Application.Entities.questions.AssignedQuestion;
import Application.Entities.users.Group;
import Application.Entities.users.MobilePlayer;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "running_tiles")
public class RunningTile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permanent_tile_id")
    private Tile tile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "controlling_group_id")
    private Group controllingGroup;
    
    @OneToOne
    @JoinColumn(name = "answering_player_id", referencedColumnName = "id")
    private MobilePlayer answeringPlayer;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "active_question")
    private AssignedQuestion activeQuestion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answering_group_id")
    private Group answeringGroup;
    
    @Column(name = "numberOfCorrectAnswers")
    private int numberOfCorrectAnswers;


    public RunningTile() {
    }

    public RunningTile(Tile tile) {
        this.tile = tile;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }



    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }


    public Group getControllingGroup() {
        return controllingGroup;
    }

    public RunningTile setControllingGroup(Group controllingGroup) {
        this.controllingGroup = controllingGroup;
        return this;
    }
    
    public MobilePlayer getAnsweringPlayer() {
        return answeringPlayer;
    }
    
    public RunningTile setAnsweringPlayer(MobilePlayer answeringPlayer) {
        this.answeringPlayer = answeringPlayer;
        return this;
    }
    
    public AssignedQuestion getActiveQuestion() {
        return activeQuestion;
    }
    
    public RunningTile setActiveQuestion(AssignedQuestion activeQuestion) {
        this.activeQuestion = activeQuestion;
        return this;
    }
    
    public Group getAnsweringGroup() {
        return answeringGroup;
    }
    
    public RunningTile setAnsweringGroup(Group answeringGroup) {
        this.answeringGroup = answeringGroup;
        return this;
    }
    
    public int getNumberOfCorrectAnswers() {
        return numberOfCorrectAnswers;
    }
    
    public RunningTile setNumberOfCorrectAnswers(int numberOfCorrectAnswers) {
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
        return this;
    }
    
    public int incrementNumberOfCorrectAnswers() {
        return this.numberOfCorrectAnswers++;
    }
    
    public boolean isAllQuestionsAnswered() {
        return switch (tile.getDifficultyLevel()) {
            case 5 -> numberOfCorrectAnswers == 3;
            case 4, 3 -> numberOfCorrectAnswers == 2;
            case 2, 1 -> numberOfCorrectAnswers == 1;
            default -> true;
        };
    }
}
