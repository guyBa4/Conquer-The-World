package Application.Entities;

import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;

import static java.util.logging.Logger.getLogger;

import Application.Repositories.AnswerRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;
import java.util.Map;
import java.util.logging.Logger;

@Entity
@Table(name = "running_game_instances")
public class RunningGameInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "running_id", nullable = false, unique = true)
    private UUID runningId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private List<MobilePlayer> mobilePlayers;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private List<Group> groups;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    private List<RunningTile> tiles;

    @Transient
    private static Logger LOG = getLogger(RunningGameInstance.class.toString());


    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_instance_id")
    private GameInstance gameInstance;

    public RunningGameInstance(){}

    public RunningGameInstance(GameInstance gameInstance) {     // Copy Constructor
        this.gameInstance = gameInstance;
        runningId = UUID.randomUUID();
        this.mobilePlayers = new LinkedList<>();
        this.tiles = new LinkedList<>();
        for(Tile tile : gameInstance.getMap().getTiles())
            tiles.add(new RunningTile(tile));
        this.status = gameInstance.getStatus();
        this.code = generateGameCode(6);
        this.initGroups();
    }

    private void initGroups() {
        this.groups = new LinkedList<>();
        for (int i = 1; i<=this.gameInstance.getNumberOfGroups(); i++){
            this.groups.add(new Group(i, this));
        }
    }

    private String generateGameCode(int length){
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generates a random number between 0 and 9
            sb.append(digit);
        }
        return sb.toString();
    }

    public UUID getRunningId() {
        return runningId;
    }

    public void setRunningId(UUID runningId) {
        this.runningId = runningId;
    }

    public List<MobilePlayer> getMobilePlayers() {
        return mobilePlayers;
    }
    public void setMobilePlayers(List<MobilePlayer> mobilePlayers) {
        this.mobilePlayers = mobilePlayers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<RunningTile> getTiles() {
        return this.tiles;
    }

    public void setTiles(List<RunningTile> tiles) {
        this.tiles = tiles;
    }

    public RunningTile getTileById(UUID runningTileId) {
        List<RunningTile> candidates = tiles.stream().filter((tile) -> tile.getId().equals(runningTileId)).toList();
        if (candidates.isEmpty())
            return null;
        return candidates.get(0);
    }

    public void addMobilePlayer(MobilePlayer mobilePlayer){
        mobilePlayers.add(mobilePlayer);
        if (status.equals(GameStatus.STARTED)) {
            Group group = getSmallestGroup();
            mobilePlayer.setGroup(group);
            group.addMobilePlayer(mobilePlayer);
            LOG.info("Added new mobile player to group " + group.getNumber());
        }
    }

    private Group getSmallestGroup() {
        Group smallestGroup = groups.get(0);
        for ( Group group : groups){
            if (group.getSize() < smallestGroup.getSize())
                smallestGroup = group;
        }
        return smallestGroup;

    }


    public AssignedQuestion getQuestion(UUID runningTileId, int groupNumber) {
        Group group = getGroupByNumber(groupNumber);
        RunningTile runningTile = getTileById(runningTileId);
        if (runningTile != null) {
            boolean isNeighbour = checkTileIsNeighbor(runningTile, groupNumber);
            if (isNeighbour) {
                int difficulty = runningTile.getTile().getDifficultyLevel();
                return group.generateQuestionFromQueue(runningTile.getTile().getDifficultyLevel());
            }
            else {
                LOG.warning("Selected tile is not a neighbor for the group.");
                throw new IllegalArgumentException("Selected tile is not a neighbor for the group.");
            }
        } else {
            LOG.warning("Could not find passed running tile.");
            throw new IllegalArgumentException("Could not find passed running tile.");
        }
    }

    private boolean checkTileIsNeighbor(RunningTile target, int group) {
        List<RunningTile> groupTiles = tiles.stream().filter((tile) -> tile.getControllingGroup().getNumber() == group).toList();
        return groupTiles.stream().anyMatch((tile) -> target.getTile().getNeighbors().contains(tile.getTile()));
    }

    public boolean checkAnswer(String tileId, Group group, UUID questionId, String answer, AnswerRepository answerRepository) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Failed to find answers for question " + questionId);
        }
        answers = answers.stream().filter(Answer::getCorrect).toList();
        if (!answers.isEmpty() && answers.get(0) != null && answers.get(0).getAnswerText().equals(answer)) {
            RunningTile tile = null;
            for(RunningTile runningTile : tiles){
                if (runningTile.getId().equals(UUID.fromString(tileId))) {
                    tile = runningTile;
                    break;
                }
            }
            if (tile == null)
                throw new IllegalArgumentException("Failed to find tile by tileId.");
            tile.setControllingGroup(group);
            group.addScore(tile.getTile().getDifficultyLevel());
            return true;
        }
        return false;
    }

    public MobilePlayer getPlayer(UUID userId) {
        for (MobilePlayer mobilePlayer : mobilePlayers){
            if (mobilePlayer.getId().equals(userId)){
                return mobilePlayer;
            }
        }
        return null;
    }

    public void assignGroups() {
        if (gameInstance.getGroupAssignmentProtocol().equals(GroupAssignmentProtocol.RANDOM)) {
            assignGroupsRandom();
        }
    }

    private void assignGroupsRandom() {
        int numberOfGroups = gameInstance.getNumberOfGroups();
        int groupToAssign = 0;
        for (MobilePlayer player : getMobilePlayers()) {
            Group group = groups.get(groupToAssign);
            player.setGroup(group);
            group.addMobilePlayer(player);
            groupToAssign = groupToAssign == numberOfGroups-1 ? 0 : groupToAssign + 1;
        }
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public String getName() {
        return gameInstance.getName();
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }
    public void setGameInstance(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public RunningGameInstance setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }

    public void initStartingPositions() {
        List<Tile> startingPositions = gameInstance.getStartingPositions();
        if (startingPositions == null || startingPositions.isEmpty() || startingPositions.size() < gameInstance.getNumberOfGroups())
            throw new RuntimeException("Starting position initialization failed - starting positions either null or do not match number of groups");
        List<RunningTile> startingTiles = tiles.stream().filter((tile) -> startingPositions.contains(tile.getTile())).toList();
        int tilePointer = 0;
        for (Group group: groups){
            startingTiles.get(tilePointer).setControllingGroup(group);
            tilePointer++;
        }
    }
    public Questionnaire getQuestionnaire(){
        return this.gameInstance.getQuestionnaire();
    }

    public Group getGroupByNumber(int groupNumber){
        for (Group group : groups){
            if (group.getNumber() == groupNumber)
                return group;
        }
        return null;
    }
}
