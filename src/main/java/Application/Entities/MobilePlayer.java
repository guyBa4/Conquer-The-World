package Application.Entities;

import java.util.UUID;

public class MobilePlayer {
    private UUID uuid;
    private String name;

    public MobilePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
