package Application.Entities;

import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;
import Application.Enums.TileType;
import static java.util.logging.Logger.getLogger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
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

    @Column(name = "code")
    private String code;

    @Column(name = "status")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    private List<RunningTile> tiles;

    @Transient
    private static Logger LOG;

    @Transient
    @PersistenceContext
    private EntityManager entityManager;

    //    @Transient
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "Game_instance_id")
    private GameInstance gameInstance;

    public RunningGameInstance(){}

    public RunningGameInstance(GameInstance gameInstance) {//copy contractor
        this.gameInstance = gameInstance;
        runningId = UUID.randomUUID();
        LOG = getLogger(this.getClass().toString());
        this.code = code;
        this.mobilePlayers = new LinkedList<>();
        this.tiles = new LinkedList<>();
        for(Tile tile : gameInstance.getMap().getTiles())
            tiles.add(new RunningTile(tile));
        this.status = gameInstance.getStatus();
        this.code = generateGameCode(6);
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

    public void addMobilePlayer(MobilePlayer mobilePlayer){
        mobilePlayers.add(mobilePlayer);
        if (gameInstance.getStatus().equals(GameStatus.STARTED.toString())) {
            int group = getSmallestGroup();
            mobilePlayer.setGroup(group);
            LOG.info("Added mobile player " + mobilePlayer.getName() + " to group " + group);
        }
    }
    
    private int getSmallestGroup() {
        Map<Integer, Integer> groupSizes = new HashMap<>();
        for (int i = 1; i <= gameInstance.getNumberOfGroups(); i++)
            groupSizes.put(i, 0);
        for (MobilePlayer player : getMobilePlayers()) {
            int playerGroup = player.getGroup();
            int groupSize = groupSizes.get(playerGroup);
            groupSizes.put(playerGroup, groupSize + 1);
        }
        int smallestGroupSize = Integer.MAX_VALUE;
        int smallestGroup = 0;
        for (Map.Entry<Integer, Integer> group : groupSizes.entrySet()) {
            if (group.getValue() < smallestGroupSize) {
                smallestGroupSize = group.getValue();
                smallestGroup = group.getKey();
            }
        }
        return smallestGroup;
    }
    
    public AssignedQuestion getQuestion(int difficulty) {
        List<AssignedQuestion> questionList = gameInstance.getQuestionnaire().getQuestions().stream().filter((question) -> (question.getQuestion().getDifficulty() == difficulty)).toList();
        int i = (int) (Math.random()*questionList.size());
        return questionList.get(i);
    }

    public boolean checkAnswer(String tileId, int group, UUID questionId, String answer) {
        List<AssignedQuestion> questionList = gameInstance.getQuestionnaire().getQuestions().stream().filter((question) -> (question.getId().equals(questionId))).toList();
        if (questionList.get(0).getQuestion().getAnswers().equals(answer)) {
            RunningTile tile = null;
            for(RunningTile runningTile : tiles){
                if (runningTile.getTile().getId().equals(tileId))
                    tile = runningTile;
            }
            if (tile == null)
                throw new IllegalArgumentException("Failed to find tile by tileId.");
            tile.setControllingGroup(group);
            return true;
        }
        return false;
    }
    
    public MobilePlayer getPlayer(UUID userId) {
        for (MobilePlayer mobilePlayer : mobilePlayers){
            if (mobilePlayer.getUuid().equals(userId)){
                return mobilePlayer;
            }
        }
        return null;
    }
    
    public void assignGroups() {
        if (gameInstance.getGroupAssignmentProtocol().equals(GroupAssignmentProtocol.RANDOM.toString())) {
            assignGroupsRandom();
        }
    }
    
    private void assignGroupsRandom() {
        int numberOfGroups = gameInstance.getNumberOfGroups();
        int groupToAssign = 1;
        for (MobilePlayer player : getMobilePlayers()) {
            player.setGroup(groupToAssign);
            groupToAssign = groupToAssign == numberOfGroups ? 1 : groupToAssign + 1;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public String getName() {
        return gameInstance.getName();
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public void setGameInstance(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public java.util.Map<String, String> toJsonMap() {
        java.util.Map<String, String>  jsonMap = gameInstance.toJsonMap();
        jsonMap.put("code", code);
        jsonMap.put("runningId", this.runningId.toString());
        jsonMap.put("status", this.status);
        return jsonMap;
    }
}
