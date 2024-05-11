package Application.Entities;

import Application.Enums.TileType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tiles")
public class Tile {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tile_type")
    private TileType tileType;

    @Column(name = "difficulty_level")
    private int difficultyLevel;

    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions;

    public Tile(){

    }

    public Tile(String id, TileType tileType, int controllingGroup, int difficultyLevel, String dimensions) {
        this.id = id;
        this.tileType = tileType;
        this.difficultyLevel = difficultyLevel;
        this.dimensions = dimensions;
    }
    // Copy constructor
    public Tile(Tile other) {
        this.id = other.id;
        this.tileType = other.tileType;
        this.difficultyLevel = other.difficultyLevel;
        this.dimensions = other.dimensions;
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
