package Application.Entities.games;
import Application.Entities.games.GameConfiguration;
import Application.Entities.questions.Questionnaire;
import Application.Entities.users.User;
import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;
import Application.Response;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "game_instances")
public class GameInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // Assuming a many-to-one relationship with User
    private User host;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id", nullable = true) // Allowing null values
    @JsonIgnore
    private Questionnaire questionnaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "map_id")
    @JsonIgnore
    private GameMap gameMap;

    @ManyToMany
    @JoinTable(
            name = "starting_positions",
            joinColumns = @JoinColumn(name = "game_instance_id"),
            inverseJoinColumns = @JoinColumn(name = "tile_id")
    )
    @JsonIgnore
    private List<Tile> startingPositions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_created")
    private Date timeCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_last_updated")
    private Date timeLastUpdated;

    @Column(name = "description")
    private String description;

    @Column(name = "shared")
    private boolean shared;
    
    @OneToOne(mappedBy = "gameInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private GameConfiguration configuration;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_tags", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "tag")
    private Set<String> tags;

    public GameInstance(){
    }

    public GameInstance(User host, Questionnaire questionnaire, GameMap gameMap, GameStatus status, int numberOfGroups,
                        String name, String description, GroupAssignmentProtocol groupAssignmentProtocol, int gameTime,
                        boolean shared, int questionTimeLimit, List<Tile> startingPositions, boolean canReconquerTiles,
                        boolean simultaneousConquering, boolean multipleQuestionPerTile) {
        this.configuration = new GameConfiguration()
                .setGameInstance(this)
                .setQuestionTimeLimit(questionTimeLimit)
                .setGameTime(gameTime)
                .setGroupAssignmentProtocol(groupAssignmentProtocol)
                .setNumberOfGroups(numberOfGroups)
                .setCanReconquerTiles(canReconquerTiles)
                .setMultipleQuestionsPerTile(multipleQuestionPerTile)
                .setSimultaneousConquering(simultaneousConquering);
        this.host = host;
        this.questionnaire = questionnaire;
        this.gameMap = gameMap;
        this.startingPositions = startingPositions;
        this.status = status;
        this.timeCreated = new Time(new Date().getTime());
        this.timeLastUpdated = new Time(new Date().getTime());
        this.name = name;
        this.description = description;
        this.shared = shared;
    }

    public GameInstance(GameInstance original) {
        this.configuration = original.configuration;
        this.host = original.host;
        this.questionnaire = original.questionnaire;
        this.gameMap = original.gameMap;
        this.status = original.status;
        this.timeCreated = original.timeCreated;
        this.timeLastUpdated = original.timeLastUpdated;
        this.name = original.name;
        this.description = original.description;
        this.shared = original.shared;
        this.startingPositions = original.startingPositions;
    }

    public GameInstance(User creator, Questionnaire questionnaire, GameMap gameMap, GameStatus created,
                        int numberOfGroups, String title, String description,
                        GroupAssignmentProtocol groupAssignmentProtocol, int gameTime, boolean shared,
                        int questionTimeLimit, boolean canReconquerTiles,
                        boolean simultaneousConquering, boolean multipleQuestionPerTile) {
        this.configuration = new GameConfiguration()
                .setGameInstance(this)
                .setQuestionTimeLimit(questionTimeLimit)
                .setGameTime(gameTime)
                .setGroupAssignmentProtocol(groupAssignmentProtocol)
                .setNumberOfGroups(numberOfGroups)
                .setCanReconquerTiles(canReconquerTiles)
                .setMultipleQuestionsPerTile(multipleQuestionPerTile)
                .setSimultaneousConquering(simultaneousConquering);
        this.host = creator;
        this.questionnaire = questionnaire;
        this.gameMap = gameMap;
        this.startingPositions =new LinkedList<>();
        this.status = created;
        this.timeCreated = new Time(new Date().getTime());
        this.timeLastUpdated = new Time(new Date().getTime());
        this.name = title;
        this.description = description;
        this.shared = shared;
    }

    public GameInstance(User creator, Questionnaire questionnaire, GameMap gameMap, GameStatus created,
                        int numberOfGroups, String title, String description,
                        GroupAssignmentProtocol groupAssignmentProtocol, int gameTime, boolean shared,
                        int questionTimeLimit, boolean canReconquerTiles,
                        boolean simultaneousConquering, boolean multipleQuestionPerTile, Set<String> tags) {
        this.configuration = new GameConfiguration()
                .setGameInstance(this)
                .setQuestionTimeLimit(questionTimeLimit)
                .setGameTime(gameTime)
                .setGroupAssignmentProtocol(groupAssignmentProtocol)
                .setNumberOfGroups(numberOfGroups)
                .setCanReconquerTiles(canReconquerTiles)
                .setMultipleQuestionsPerTile(multipleQuestionPerTile)
                .setSimultaneousConquering(simultaneousConquering);
        this.host = creator;
        this.questionnaire = questionnaire;
        this.gameMap = gameMap;
        this.startingPositions =new LinkedList<>();
        this.status = created;
        this.timeCreated = new Time(new Date().getTime());
        this.timeLastUpdated = new Time(new Date().getTime());
        this.name = title;
        this.description = description;
        this.shared = shared;
        this.tags = tags;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public GameMap getMap() {
        return gameMap;
    }

    public void setMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public List<Tile> getStartingPositions() {
        return startingPositions;
    }

    public void setStartingPositions(List<Tile> startingPositions) {
        this.startingPositions = startingPositions;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getTimeLastUpdated() {
        return timeLastUpdated;
    }

    public void setTimeLastUpdated(Date timeLastUpdated) {
        this.timeLastUpdated = timeLastUpdated;
    }

    public void addStartingPosition(Tile tile) {
        this.startingPositions.add(tile);
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public GameInstance setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        return this;
    }
    
    public GameConfiguration getConfiguration() {
        return configuration;
    }
    
    public GameInstance setConfiguration(GameConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }


    @JsonProperty("map")
    public Map<String, Object> getGameMapSummary() {
        GameMap gameMap = this.getGameMap();
        if (gameMap != null) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("id", gameMap.getId());
            summary.put("name", gameMap.getName());
            return summary;
        } else {
            return null;
        }
    }
    @JsonProperty("startingPositions")
    public List<Map<String, Object>> getStartingPositionsSummary() {
        List<Tile> startingPositions = this.getStartingPositions();
        if (gameMap != null) {
            List<Map<String, Object>> summary = new LinkedList<>();
            for (Tile tile : startingPositions){
                Map<String, Object> leanTile = new HashMap<>();
                leanTile.put("id", tile.getId());
                leanTile.put("name", tile.getName());
                summary.add(leanTile);
            }
            return summary;
        } else {
            return null;
        }
    }
    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void addTags(String tag) {
        this.tags.add(tag);
    }

}
