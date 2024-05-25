package Application.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Entity
@Table(name = "mobile_players")
public class MobilePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private RunningGameInstance runningGameInstance;

    @Column(name = "ready")
    private boolean ready;

    public MobilePlayer(){
        ready = false;
    }
    public MobilePlayer(String name, RunningGameInstance runningGameInstance) {
        this.name = name;
        ready = false;
        this.runningGameInstance = runningGameInstance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RunningGameInstance getRunningGameInstance() {
        return runningGameInstance;
    }

    public void setRunningGameInstance(RunningGameInstance runningGameInstance) {
        this.runningGameInstance = runningGameInstance;
    }

}
