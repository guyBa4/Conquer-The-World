package Application.Entities;

import java.util.UUID;

public class MobilePlayer {
    private UUID uuid;
    private String name;
    private int group;

    public MobilePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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
}
