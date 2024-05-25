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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permanent_tile_id")
    private Tile tile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "controlling_group_id")
    private Group controllingGroup;


    public RunningTile() {
    }

    public RunningTile(Tile tile) {
        this.tile = tile;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }



    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }


    public Group getControllingGroup() {
        return controllingGroup;
    }

    public void setControllingGroup(Group controllingGroup) {
        this.controllingGroup = controllingGroup;
    }
}
