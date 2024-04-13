package Application.Entities;
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


    @Column(name = "ready")
    private boolean ready;

    public MobilePlayer(){
        ready = false;
        group = 0;
    }
    public MobilePlayer(String name) {
        this.name = name;
        ready = false;
        group = 0;
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
}
