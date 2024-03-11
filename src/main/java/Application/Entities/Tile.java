package Application.Entities;

import Application.Enums.TileType;

import java.util.UUID;

public class Tile {
    
    private String id;
    private TileType tileType;
    private int controllingGroup;   // Set to be the group number of the occupying group, 0 if not controlled and -1 if sea tile or neutral tile
    private int difficultyLevel;
    private String dimensions;
    
    public Tile(){

    }

    public Tile(String id, TileType tileType, int controllingGroup, int difficultyLevel, String dimensions) {
        this.id = id;
        this.tileType = tileType;
        this.controllingGroup = controllingGroup;
        this.difficultyLevel = difficultyLevel;
        this.dimensions = dimensions;
    }

    public String getId() {
        return id;
    }

    public Tile setId(String id) {
        this.id = id;
        return this;
    }

    public TileType getTileType() {
        return tileType;
    }

    public Tile setTileType(TileType tileType) {
        this.tileType = tileType;
        return this;
    }

    public int getControllingGroup() {
        return controllingGroup;
    }

    public Tile setControllingGroup(int controllingGroup) {
        this.controllingGroup = controllingGroup;
        return this;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public Tile setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        return this;
    }
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
}
