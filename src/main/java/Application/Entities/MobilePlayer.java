package Application.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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

    @Column(name = "group_number")
    private int group;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private RunningGameInstance runningGameInstance;


    @Column(name = "ready")
    private boolean ready;

    public MobilePlayer(){
        ready = false;
        group = 0;
    }
    public MobilePlayer(String name, RunningGameInstance runningGameInstance) {
        this.name = name;
        ready = false;
        group = 0;
        this.runningGameInstance = runningGameInstance;
    }

    public UUID getUuid() {
        return id;
    }

    public void setUuid(UUID uuid) {
        this.id = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getGroup() {
        return group;
    }
    
    public void setGroup(int group) {
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
