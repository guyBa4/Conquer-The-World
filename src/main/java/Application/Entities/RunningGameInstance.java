package Application.Entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunningGameInstance extends GameInstance{


    private UUID runningId;
    private Map<UUID, MobilePlayer> idTomobilePlayer;
    private String code;
    private Map<UUID, Tile> tiles;

    public RunningGameInstance(GameInstance gameInstance) {
        super(gameInstance); //copy contractor
        runningId = UUID.randomUUID();
        this.code = String.valueOf(Math.round(Math.random()*1000000));
        this.idTomobilePlayer = new HashMap<>();
        this.tiles = new HashMap<>();
    }

    public UUID getRunningId() {
        return runningId;
    }

    public void setRunningId(UUID runningId) {
        this.runningId = runningId;
    }
    public Map<UUID, MobilePlayer> getIdToMobilePlayer() {
        return idTomobilePlayer;
    }

    public void setIdToMobilePlayer(Map<UUID, MobilePlayer> idTomobilePlayer) {
        this.idTomobilePlayer = idTomobilePlayer;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<UUID, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Map<UUID, Tile> tiles) {
        this.tiles = tiles;
    }

    public void addMobilePlayer(MobilePlayer mobilePlayer){
        UUID id = mobilePlayer.getUuid();
        idTomobilePlayer.put(id, mobilePlayer);
    }
    public void getMobilePlayers(MobilePlayer mobilePlayer){
        UUID id = mobilePlayer.getUuid();
        idTomobilePlayer.put(id, mobilePlayer);
    }
}
