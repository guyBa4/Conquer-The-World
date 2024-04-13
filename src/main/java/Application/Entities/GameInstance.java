package Application.Entities;
import Application.Response;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private Map map;

    @ElementCollection
    @CollectionTable(name = "map_starting_positions", joinColumns = @JoinColumn(name = "map_id"))
    @Column(name = "starting_position")
    private List<String> startingPositions;

    @Column(name = "status")
    private String status;

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

//    @Enumerated(EnumType.STRING)
    @Column(name = "group_assignment_protocol")
    private String groupAssignmentProtocol;

    @Column(name = "game_time")
    private int gameTime;

    @Column(name = "shared")
    private boolean shared;

    @Column(name = "question_time_limit")
    private int questionTimeLimit;



    public GameInstance(){
    }

    public GameInstance(User host, Questionnaire questionnaire, Map map, String status, int numberOfGroups, String name, String description, String groupAssignmentProtocol, int gameTime, boolean shared, int questionTimeLimit, List<String> startingPositions) {
        this.host = host;
        this.questionnaire = questionnaire;
        this.map = map;
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
        this.map = original.map;
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

    public static Response<GameInstance> fromJson(JSONObject jsonObject){
        GameInstance gameInstance = new GameInstance();
        gameInstance.host = new User().setId(UUID.fromString(jsonObject.getString("host")));
//        this.questionnaire = questionnaire;
//        this.map = map;
        gameInstance.status = jsonObject.getString("status");
//        this.timeCreated = timeCreated;
//        this.timeLastUpdated = timeLastUpdated;
        gameInstance.numberOfGroups = jsonObject.getInt("numberOfGroups");
        gameInstance.name = jsonObject.getString("name");
        gameInstance.description = jsonObject.getString("description");
        gameInstance.groupAssignmentProtocol = jsonObject.getString("groupAssignmentProtocol");
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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getGroupAssignmentProtocol() {
        return groupAssignmentProtocol;
    }

    public void setGroupAssignmentProtocol(String groupAssignmentProtocol) {
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

    public List<String> getStartingPositions() {
        return startingPositions;
    }

    public void setStartingPositions(List<String> startingPositions) {
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


}
