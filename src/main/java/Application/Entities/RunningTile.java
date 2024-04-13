package Application.Entities;

import Application.Enums.TileType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "running_tiles")
public class RunningTile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tile_type")
    private TileType tileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permanent_tile_id")
    private Tile tile;


    public RunningTile() {
    }

    public RunningTile(Tile tile) {
        this.tile = tile;
        this.tileType = tile.getTileType();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
