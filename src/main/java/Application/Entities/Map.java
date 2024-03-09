package Application.Entities;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "maps")
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or GenerationType.IDENTITY
    @Column(name = "Id", nullable = false, unique = true)
    private UUID id;


    public Map(){

    }

    public Map(UUID id){
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
