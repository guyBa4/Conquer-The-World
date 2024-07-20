package Application.Entities.users;
import Application.Entities.games.RunningGameInstance;
import Application.Events.EventRecipient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mobile_players")
public class MobilePlayer implements EventRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    @JsonManagedReference
    @JsonIgnoreProperties({"mobilePlayers"})
    private Group group;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "running_game_instance_id")
    @JsonIgnore
    private RunningGameInstance runningGameInstance;

    @Column(name = "ready")
    private boolean ready;

    @JsonIgnore
    @OneToOne(mappedBy = "mobilePlayer")
    private PlayerStatistics playerStatistics;
    
    @ManyToOne
    @JoinColumn(name = "mobile_user_id")
    @JsonIgnoreProperties({"mobilePlayers"})
    private MobileUser mobileUser;
    
    public MobilePlayer(){
        this.ready = false;
        this.playerStatistics = new PlayerStatistics();
    }
    public MobilePlayer(String name, RunningGameInstance runningGameInstance) {
        this.name = name;
        this.ready = false;
        this.runningGameInstance = runningGameInstance;
        this.playerStatistics = new PlayerStatistics();
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobilePlayer that = (MobilePlayer) o;
        return id.equals(that.id);
    }

    public PlayerStatistics getPlayerStatistics() {
        return playerStatistics;
    }

    public MobilePlayer setPlayerStatistics(PlayerStatistics playerStatistics) {
        this.playerStatistics = playerStatistics;
        return this;
    }
    
    public MobileUser getMobileUser() {
        return mobileUser;
    }
    
    public MobilePlayer setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
        return this;
    }
}
