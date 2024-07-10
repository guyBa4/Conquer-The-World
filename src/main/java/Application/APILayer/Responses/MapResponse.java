package Application.APILayer.Responses;

import Application.Entities.games.RunningTile;

import java.util.List;

public class MapResponse {
    
    private List<RunningTile> tiles;
    private int eventIndex;
    
    public List<RunningTile> getTiles() {
        return tiles;
    }
    
    public MapResponse setTiles(List<RunningTile> tiles) {
        this.tiles = tiles;
        return this;
    }
    
    public int getEventIndex() {
        return eventIndex;
    }
    
    public MapResponse setEventIndex(int eventIndex) {
        this.eventIndex = eventIndex;
        return this;
    }
}
