package Application.Entities.games;

import Application.Entities.questions.Answer;
import Application.Entities.questions.AssignedQuestion;
import Application.Entities.questions.Questionnaire;
import Application.Entities.users.Group;
import Application.Entities.users.MobilePlayer;
import Application.Entities.users.PlayerStatistics;
import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;

import static java.util.logging.Logger.getLogger;

import Application.DataAccessLayer.Repositories.AnswerRepository;
import Application.Enums.TileType;
import jakarta.persistence.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@Entity
@Table(name = "running_game_instances")
public class RunningGameInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "running_id", nullable = false, unique = true)
    private UUID runningId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    private List<MobilePlayer> mobilePlayers;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    private List<Group> groups;

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    private List<RunningTile> tiles;


    @OneToMany(mappedBy = "runningGameInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerStatistics> playerStatistics;

    @OneToOne(mappedBy = "runningGameInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameStatistic gameStatistic;

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
        for (int i = 0; i<=this.gameInstance.getConfiguration().getNumberOfGroups(); i++){
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

    public List<PlayerStatistics> getPlayerStatistics() {
        return playerStatistics;
    }

    public RunningGameInstance setPlayerStatistics(List<PlayerStatistics> playerStatistics) {
        this.playerStatistics = playerStatistics;
        return this;
    }
    public void addPlayerStatistic(PlayerStatistics playerStatistics){
        this.playerStatistics.add(playerStatistics);
    }
    public RunningTile getTileById(UUID runningTileId) {
        List<RunningTile> candidates = tiles.stream().filter((tile) -> tile.getId().equals(runningTileId)).toList();
        if (candidates.isEmpty())
            return null;
        return candidates.get(0);
    }

    public void addMobilePlayer(MobilePlayer mobilePlayer){
        mobilePlayers.add(mobilePlayer);
        PlayerStatistics playerStatistics = new PlayerStatistics(mobilePlayer, this);
        mobilePlayer.setPlayerStatistics(playerStatistics);
        this.playerStatistics.add(playerStatistics);
        if (status.equals(GameStatus.STARTED)) {
            Group group = getSmallestGroup();
            mobilePlayer.setGroup(group);
            group.addMobilePlayer(mobilePlayer);
            LOG.info("Added new mobile player to group " + group.getNumber());
        }
    }

    private Group getSmallestGroup() {
        Group smallestGroup = groups.get(0);
        if(smallestGroup.getNumber() == 0)
            smallestGroup = groups.get(1);
        for ( Group group : groups){
            if (group.getSize() < smallestGroup.getSize())
                if(group.getNumber() != 0)
                    smallestGroup = group;
        }
        return smallestGroup;

    }

    public AssignedQuestion getQuestion(UUID runningTileId, int groupNumber, MobilePlayer player) {
        Group group = getGroupByNumber(groupNumber);
        RunningTile runningTile = getTileById(runningTileId);
        if (runningTile != null) {
            boolean isNeighbour = checkTileIsGroupNeighbor(runningTile, groupNumber);
            if (isNeighbour) {
                if(runningTile.getAnsweringGroup() == null) {
                    int difficulty = runningTile.getTile().getDifficultyLevel();
                    AssignedQuestion question = group.generateQuestionFromQueue(difficulty);
                    runningTile.setAnsweringPlayer(player).setAnsweringGroup(group).setActiveQuestion(question);
                    return question;
                }
                else if (runningTile.getAnsweringGroup().equals(group))
                    return runningTile.getActiveQuestion();
                else {
                    LOG.warning("Tile is being attempted by other group");
                    throw new IllegalArgumentException("Tile is being attempted by other group");
                }
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
    
    private boolean checkTilesAreNeighbors(Tile source, Tile target) {
        return source.getNeighbors().stream().anyMatch((tile) -> {
            if (tile.getId().equals(target.getId())) {
                return true;
            }
            else if (tile.getTileType().equals(TileType.SEA)) {
                return checkTilesAreNeighbors(tile, target);
            }
            else return false;
        });
    }

    private boolean checkTileIsGroupNeighbor(RunningTile target, int group) {
        Set<Tile> groupNeighbors = new HashSet<>();
        tiles.forEach((tile) -> {
            if (tile.getControllingGroup().getNumber() == group) {
                tile.getTile().getNeighbors().forEach((neighbor) -> {
                    groupNeighbors.add(neighbor);
                    if (neighbor.getTileType().name().equals(TileType.SEA.name())) {
                        groupNeighbors.addAll(neighbor.getNeighbors());
                    }
                });
            }
        });
        return groupNeighbors.contains(target.getTile());
    }

    public boolean checkAnswer(RunningTile tile, MobilePlayer player, UUID questionId, String answer, AnswerRepository answerRepository) throws IOException {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        if (tile == null) {
            LOG.warning("Failed to find tile by tileId.");
            throw new IllegalArgumentException("Failed to find tile by tileId.");
        }
        if (tile.getAnsweringPlayer() == null || !tile.getAnsweringPlayer().equals(player)) {
            LOG.warning("Player " + player.getName() + " with ID " + player.getId() +
                    " can not answer question for running tile with ID " + tile.getId());
            throw new IllegalArgumentException("Player is not the answering player and can not answer this tile's question.");
        }
        if (answers == null || answers.isEmpty()) {
            LOG.warning("Failed to find answers for question " + questionId);
            throw new IllegalArgumentException("Failed to find answers for question " + questionId);
        }
        answers = answers.stream().filter(Answer::getCorrect).toList();
        return !answers.isEmpty() && answers.get(0) != null && answers.get(0).getAnswerText().trim().equalsIgnoreCase(answer.trim());
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
        if (gameInstance.getConfiguration().getGroupAssignmentProtocol().equals(GroupAssignmentProtocol.RANDOM)) {
            assignGroupsRandom();
        }
    }

    private void assignGroupsRandom() {
        int numberOfGroups = gameInstance.getConfiguration().getNumberOfGroups();
        int groupToAssign = 0;
        List<Group> groupsToAssign = groups.stream().filter((group)-> group.getNumber()!=0).toList();
        for (MobilePlayer player : getMobilePlayers()) {
            Group group = groupsToAssign.get(groupToAssign);
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
        if (startingPositions == null || startingPositions.isEmpty() || startingPositions.size() < gameInstance.getConfiguration().getNumberOfGroups())
            throw new RuntimeException("Starting position initialization failed - starting positions either null or do not match number of groups");
        List<RunningTile> startingTiles = new ArrayList<>();
        List<RunningTile> nonstartingTiles = new ArrayList<>();
        for (RunningTile rt : tiles) {
            if (startingPositions.contains(rt.getTile()))
                startingTiles.add(rt);
            else
                nonstartingTiles.add(rt);
        }
        int tilePointer = 0;
        List<Group> groupsToAssign = groups.stream().filter((group)-> group.getNumber()!=0).toList();
        for (Group group: groupsToAssign){
            startingTiles.get(tilePointer).setControllingGroup(group);
            tilePointer++;
        }
        Group zeroGroup = groups.stream()
                .filter((group) -> group.getNumber() == 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No group 0 found."));
        for (RunningTile rt : nonstartingTiles) {
            rt.setControllingGroup(zeroGroup);
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
    public GameStatistic getGameStatistics() {
        return gameStatistic;
    }

    public RunningGameInstance setGameStatistics(GameStatistic gameStatistic) {
        this.gameStatistic = gameStatistic;
        return this;
    }


}
