package Application.ServiceLayer;

import Application.Entities.AssignedQuestion;
import Application.Entities.Group;
import Application.Entities.MobilePlayer;
import Application.Entities.RunningTile;

import java.util.UUID;

public class FlatRunningTile {
    private UUID id;
    private Group controllingGroup;
    private MobilePlayer answeringPlayer;
    private AssignedQuestion activeQuestion;
    private Group answeringGroup;
    
    private FlatRunningTile(UUID id, Group controllingGroup, MobilePlayer answeringPlayer, AssignedQuestion activeQuestion, Group answeringGroup) {
        this.id = id;
        this.controllingGroup = controllingGroup;
        this.answeringPlayer = answeringPlayer;
        this.activeQuestion = activeQuestion;
        this.answeringGroup = answeringGroup;
    }
    
    public static FlatRunningTile from(RunningTile runningTile) {
        return new FlatRunningTile(runningTile.getId(),
                runningTile.getControllingGroup(),
                runningTile.getAnsweringPlayer(),
                runningTile.getActiveQuestion(),
                runningTile.getAnsweringGroup());
    }
    
    public UUID getId() {
        return id;
    }
    
    public Group getControllingGroup() {
        return controllingGroup;
    }
    
    public MobilePlayer getAnsweringPlayer() {
        return answeringPlayer;
    }
    
    public AssignedQuestion getActiveQuestion() {
        return activeQuestion;
    }
    
    public Group getAnsweringGroup() {
        return answeringGroup;
    }
}
