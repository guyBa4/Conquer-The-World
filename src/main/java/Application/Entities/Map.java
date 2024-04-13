package Application.Entities;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "maps")
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column
    private String name;

    @Column
    private boolean shared;


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    private List<Tile> tiles;

    public Map(){

    }

    public Map(String name, List<Tile> tiles, boolean shared) {
        this.name = name;
        this.tiles = tiles;
        this.shared = shared;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
