package Application.ServiceLayer;

import Application.Entities.GameInstance;
import Application.Entities.Map;
import Application.Entities.Questionnaire;
import Application.Entities.User;
import jakarta.persistence.*;

import java.util.UUID;

public class FlatGameInstance {

    private UUID id;

    private UUID hostId;

    private UUID questionnaireId;

    private UUID mapId;

    private String status;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "time_created")
//    private Date timeCreated;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "time_last_updated")
//    private Date timeLastUpdated;

    private int numberOfGroups;


    private String name;

    private String description;

    private String groupAssignmentProtocol;

    private int gameTime;

    private boolean shared;

    private int questionTimeLimit;


    public FlatGameInstance(GameInstance gameInstance){
        this.id = gameInstance.getId();
        this.hostId = gameInstance.getHost().getId();
        this.questionnaireId = gameInstance.getQuestionnaire().getId();
        this.mapId = gameInstance.getMap().getId();
        this.status = gameInstance.getStatus();
        this.numberOfGroups = gameInstance.getNumberOfGroups();
        this.name = gameInstance.getName();
        this.description = gameInstance.getDescription();
        this.groupAssignmentProtocol = gameInstance.getGroupAssignmentProtocol();
        this.gameTime = gameInstance.getGameTime();
        this.shared = gameInstance.isShared();
        this.questionTimeLimit = gameInstance.getQuestionTimeLimit();
    }
}
