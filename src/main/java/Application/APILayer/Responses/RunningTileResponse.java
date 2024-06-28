package Application.APILayer.Responses;

import Application.Entities.games.RunningTile;
import Application.Entities.questions.AssignedQuestion;

import java.util.UUID;

public class RunningTileResponse {
    private UUID id;
    private UUID answeringGroupId;
    private UUID answeringPlayerId;
    private UUID controllingGroupId;
    private AssignedQuestion activeQuestion;
    private int numberOfCorrectAnswers;
    
    public static RunningTileResponse fromRunningTile(RunningTile tile) {
        return new RunningTileResponse()
                .setId(tile.getId())
                .setAnsweringGroupId(tile.getAnsweringGroup().getId())
                .setAnsweringPlayerId(tile.getAnsweringPlayer().getId())
                .setActiveQuestion(tile.getActiveQuestion())
                .setControllingGroupId(tile.getControllingGroup().getId())
                .setNumberOfCorrectAnswers(tile.getNumberOfCorrectAnswers());
    }
    
    public UUID getId() {
        return id;
    }
    
    public RunningTileResponse setId(UUID id) {
        this.id = id;
        return this;
    }
    
    public UUID getAnsweringGroupId() {
        return answeringGroupId;
    }
    
    public RunningTileResponse setAnsweringGroupId(UUID answeringGroupId) {
        this.answeringGroupId = answeringGroupId;
        return this;
    }
    
    public UUID getAnsweringPlayerId() {
        return answeringPlayerId;
    }
    
    public RunningTileResponse setAnsweringPlayerId(UUID answeringPlayerId) {
        this.answeringPlayerId = answeringPlayerId;
        return this;
    }
    
    public UUID getControllingGroupId() {
        return controllingGroupId;
    }
    
    public RunningTileResponse setControllingGroupId(UUID controllingGroupId) {
        this.controllingGroupId = controllingGroupId;
        return this;
    }
    
    public AssignedQuestion getActiveQuestion() {
        return activeQuestion;
    }
    
    public RunningTileResponse setActiveQuestion(AssignedQuestion activeQuestion) {
        this.activeQuestion = activeQuestion;
        return this;
    }
    
    public int getNumberOfCorrectAnswers() {
        return numberOfCorrectAnswers;
    }
    
    public RunningTileResponse setNumberOfCorrectAnswers(int numberOfCorrectAnswers) {
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
        return this;
    }
}
