package Application.Entities;

import Application.Enums.GroupAssignmentProtocol;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "game_configurations")
public class GameConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "game_instance_id", referencedColumnName = "id")
    private GameInstance gameInstance;

    @Column(name = "can_reconquer_tiles")
    private boolean canReconquerTiles;              // If true, groups can conquer each other's tiles

    @Column(name = "multiple_questions_per_tile")
    private boolean multipleQuestionsPerTile;       // If true then tiles with difficulty 1-2 will have to be answered
                                                    // with one question, tiles with difficulty 3-4 will have to be
                                                    // answered with 2 questions and tiles with difficulty 5 will have
                                                    // to be answered with 3 questions

    @Column(name = "simultaneous_conquering")
    private boolean simultaneousConquering;         // If true, players from different groups will be able to attack tiles
                                                    // at the same time
    
    @Enumerated(EnumType.STRING)
    @Column(name = "group_assignment_protocol")
    private GroupAssignmentProtocol groupAssignmentProtocol;
    
    @Column(name = "game_time")
    private int gameTime;
    
    @Column(name = "number_of_groups")
    private int numberOfGroups;
    
    @Column(name = "question_time_limit")
    private int questionTimeLimit;
    
    public GameConfiguration() {
    }
    
    public GameInstance getGameInstance() {
        return gameInstance;
    }
    
    public GameConfiguration setGameInstance(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
        return this;
    }
    
    public boolean getCanReconquerTiles() {
        return canReconquerTiles;
    }
    
    public GameConfiguration setCanReconquerTiles(boolean canReconquerTiles) {
        this.canReconquerTiles = canReconquerTiles;
        return this;
    }
    
    public boolean getMultipleQuestionsPerTile() {
        return multipleQuestionsPerTile;
    }
    
    public GameConfiguration setMultipleQuestionsPerTile(boolean multipleQuestionsPerTile) {
        this.multipleQuestionsPerTile = multipleQuestionsPerTile;
        return this;
    }
    
    public boolean getSimultaneousConquering() {
        return simultaneousConquering;
    }
    
    public GameConfiguration setSimultaneousConquering(boolean simultaneousConquering) {
        this.simultaneousConquering = simultaneousConquering;
        return this;
    }
    
    public GroupAssignmentProtocol getGroupAssignmentProtocol() {
        return groupAssignmentProtocol;
    }
    
    public GameConfiguration setGroupAssignmentProtocol(GroupAssignmentProtocol groupAssignmentProtocol) {
        this.groupAssignmentProtocol = groupAssignmentProtocol;
        return this;
    }
    
    public int getGameTime() {
        return gameTime;
    }
    
    public GameConfiguration setGameTime(int gameTime) {
        this.gameTime = gameTime;
        return this;
    }
    
    public int getNumberOfGroups() {
        return numberOfGroups;
    }
    
    public GameConfiguration setNumberOfGroups(int numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
        return this;
    }
    
    public int getQuestionTimeLimit() {
        return questionTimeLimit;
    }
    
    public GameConfiguration setQuestionTimeLimit(int questionTimeLimit) {
        this.questionTimeLimit = questionTimeLimit;
        return this;
    }
}
