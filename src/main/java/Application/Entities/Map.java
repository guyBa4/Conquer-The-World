package Application.Entities;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "maps")
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private UUID id;

    @Column
    private String name;

    @ElementCollection
    @CollectionTable(name = "map_starting_positions", joinColumns = @JoinColumn(name = "map_id"))
    @Column(name = "starting_position")
    private List<String> startingPositions;

    public Map(){

    }

    public Map(UUID id){
        this.id = id;
    }

    public Map(UUID id, String name, List<String> startingPositions) {
        this.id = id;
        this.name = name;
        this.startingPositions = startingPositions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
