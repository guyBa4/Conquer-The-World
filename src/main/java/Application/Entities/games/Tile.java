package Application.Entities.games;

import Application.Enums.TileType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tiles")
public class Tile {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;


    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "tile_type")
    private TileType tileType;

    @Column(name = "difficulty_level")
    private int difficultyLevel;

    @Column(name = "dimensions", columnDefinition = "TEXT")
    private String dimensions;


    @ManyToMany
    @JoinTable(
            name = "tile_neighbors",
            joinColumns = @JoinColumn(name = "tile_id"),
            inverseJoinColumns = @JoinColumn(name = "neighbor_id")
    )
    @JsonManagedReference
    @JsonIgnoreProperties({"neighbors"})
    private List<Tile> neighbors;
    public Tile(){

    }

    public Tile(UUID id, TileType tileType, int difficultyLevel, String dimensions) {
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
        this.name = other.name;
        this.neighbors = other.neighbors;
    }

    public UUID getId() {
        return id;
    }

    public Tile setId(UUID id) {
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
    
    public Tile setDimensions(String dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tile setName(String name) {
        this.name = name;
        return this;
    }
    public List<Tile> getNeighbors() {
        return neighbors;
    }

    public Tile setNeighbors(List<Tile> neighbors) {
        this.neighbors = neighbors;
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

}
