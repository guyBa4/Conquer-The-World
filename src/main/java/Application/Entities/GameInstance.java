package Application.Entities;
import Application.Enums.GameStatus;
import Application.Enums.GroupAssignmentProtocol;
import Application.Response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JoinColumn(name = "questionnaire_id") // Assuming a many-to-one relationship with Questionnaire
    private Questionnaire questionnaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "map_id")
    private GameMap gameMap;

    @ManyToMany
    @JoinTable(
            name = "starting_positions",
            joinColumns = @JoinColumn(name = "game_instance_id"),
            inverseJoinColumns = @JoinColumn(name = "tile_id")
    )
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

    @Column(name = "number_of_groups")
    private int numberOfGroups;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_assignment_protocol")
    private GroupAssignmentProtocol groupAssignmentProtocol;

    @Column(name = "game_time")
    private int gameTime;

    @Column(name = "shared")
    private boolean shared;

    @Column(name = "question_time_limit")
    private int questionTimeLimit;



    public GameInstance(){
    }

    public GameInstance(User host, Questionnaire questionnaire, GameMap gameMap, GameStatus status, int numberOfGroups, String name, String description, GroupAssignmentProtocol groupAssignmentProtocol, int gameTime, boolean shared, int questionTimeLimit, List<Tile> startingPositions) {
        this.host = host;
        this.questionnaire = questionnaire;
        this.gameMap = gameMap;
        this.startingPositions =startingPositions;
        this.status = status;
        this.timeCreated = new Time(new Date().getTime());
        this.timeLastUpdated = new Time(new Date().getTime());
        this.numberOfGroups = numberOfGroups;
        this.name = name;
        this.description = description;
        this.groupAssignmentProtocol = groupAssignmentProtocol;
        this.gameTime = gameTime;
        this.shared = shared;
        this.questionTimeLimit = questionTimeLimit;
    }

    public GameInstance(GameInstance original) {
//        this.id = original.id;
        this.host = original.host;
        this.questionnaire = original.questionnaire;
        this.gameMap = original.gameMap;
        this.status = original.status;
        this.timeCreated = original.timeCreated;
        this.timeLastUpdated = original.timeLastUpdated;
        this.numberOfGroups = original.numberOfGroups;
        this.name = original.name;
        this.description = original.description;
        this.groupAssignmentProtocol = original.groupAssignmentProtocol;
        this.gameTime = original.gameTime;
        this.shared = original.shared;
        this.questionTimeLimit = original.questionTimeLimit;
    }

    public GameInstance(User creator, Questionnaire questionnaire, GameMap gameMap, GameStatus created, int numberOfGroups, String title, String description, GroupAssignmentProtocol groupAssignmentProtocol, int gameTime, boolean shared, int questionTimeLimit) {
        this.host = creator;
        this.questionnaire = questionnaire;
        this.gameMap = gameMap;
        this.startingPositions =new LinkedList<>();
        this.status = created;
        this.timeCreated = new Time(new Date().getTime());
        this.timeLastUpdated = new Time(new Date().getTime());
        this.numberOfGroups = numberOfGroups;
        this.name = title;
        this.description = description;
        this.groupAssignmentProtocol = groupAssignmentProtocol;
        this.gameTime = gameTime;
        this.shared = shared;
        this.questionTimeLimit = questionTimeLimit;
    }


    public static Response<GameInstance> fromJson(JSONObject jsonObject){
        GameInstance gameInstance = new GameInstance();
        gameInstance.host = new User().setId(UUID.fromString(jsonObject.getString("host")));
//        this.questionnaire = questionnaire;
//        this.map = map;
        gameInstance.status = GameStatus.valueOf(jsonObject.getString("status"));
//        this.timeCreated = timeCreated;
//        this.timeLastUpdated = timeLastUpdated;
        gameInstance.numberOfGroups = jsonObject.getInt("numberOfGroups");
        gameInstance.name = jsonObject.getString("name");
        gameInstance.description = jsonObject.getString("description");
        gameInstance.groupAssignmentProtocol = GroupAssignmentProtocol.valueOf(jsonObject.getString("groupAssignmentProtocol"));
        gameInstance.gameTime = jsonObject.getInt("gameTime");
        gameInstance.shared = jsonObject.getBoolean("shared");
        gameInstance.questionTimeLimit = jsonObject.getInt("questionTimeLimit");
        return Response.ok(gameInstance);
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


    public int getNumberOfGroups() {
        return numberOfGroups;
    }

    public void setNumberOfGroups(int numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
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

    public GroupAssignmentProtocol getGroupAssignmentProtocol() {
        return groupAssignmentProtocol;
    }

    public void setGroupAssignmentProtocol(GroupAssignmentProtocol groupAssignmentProtocol) {
        this.groupAssignmentProtocol = groupAssignmentProtocol;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public int getQuestionTimeLimit() {
        return questionTimeLimit;
    }

    public void setQuestionTimeLimit(int questionTimeLimit) {
        this.questionTimeLimit = questionTimeLimit;
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

//    public java.util.Map<String, String> toJsonMap() {
//        java.util.Map<String, String>  jsonMap = new HashMap<>();
//        jsonMap.put("id", id.toString());
//        jsonMap.put("name", name);
//        jsonMap.put("host", host.getId().toString());
//        jsonMap.put("status", status);
//        jsonMap.put("timeCreated", timeCreated.toString());
//        jsonMap.put("timeLastUpdated", timeLastUpdated.toString());
//        jsonMap.put("numberOfGroups", String.valueOf(numberOfGroups));
//        jsonMap.put("description", description);
//        jsonMap.put("groupAssignmentProtocol", groupAssignmentProtocol);
//        jsonMap.put("gameTime", String.valueOf(gameTime));
//        jsonMap.put("shared", String.valueOf(shared));
//        jsonMap.put("questionTimeLimit", String.valueOf(questionTimeLimit));
//        jsonMap.put("mapId", map.getId().toString());
//        jsonMap.put("mapName", map.getName());
//        jsonMap.put("questionnaireId", questionnaire.getId().toString());
//        jsonMap.put("questionnaireName", questionnaire.getName());
//        return jsonMap;
//    }
}
